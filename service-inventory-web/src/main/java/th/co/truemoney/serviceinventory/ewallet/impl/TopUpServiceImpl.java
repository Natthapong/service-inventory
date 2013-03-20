package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.ewallet.OTPService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.util.EncryptUtil;
import th.co.truemoney.serviceinventory.util.FeeUtil;

@Service
public class TopUpServiceImpl implements TopUpService {

	private static final Logger logger = LoggerFactory
			.getLogger(TopUpServiceImpl.class);

	@Autowired
	@Qualifier("accessTokenMemoryRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private SourceOfFundRepository sofRepo;

	@Autowired
	private DirectDebitConfig directDebitConfig;

	@Autowired
	@Qualifier("orderMemoryRepository")
	private OrderRepository orderRepo;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private OTPService otpService;

	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID,
			QuoteRequest quoteRequest, String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		if (accessToken == null)
			throw new ServiceInventoryException("90001",
					"AccessToken not found.");

		// --- get SOF List ---//
		DirectDebit sofDetail = getSourceOfFund(sourceOfFundID, accessToken);

		BigDecimal amount = quoteRequest.getAmount();
		BigDecimal minAmount = sofDetail.getMinAmount();
		BigDecimal maxAmount = sofDetail.getMaxAmount();
		if (amount.compareTo(minAmount) < 0)
			throw new ServiceInventoryException("20001",
					"amount less than min amount.");
		if (amount.compareTo(maxAmount) > 0)
			throw new ServiceInventoryException("20002",
					"amount most than max amount.");

		// --- Connect to Ewallet Client to verify amount on this
		// ewallet-account ---//
		try {
			StandardMoneyResponse verifyResponse = verifyTopupEwallet(
					quoteRequest.getAmount(), accessToken.getChannelID(),
					sofDetail.getSourceOfFundType());

		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
					"verify add money fail.", e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(
					Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
					e.getMessage(), e.getNamespace());
		}

		// --- calculate total FEE ---//
		DirectDebitConfigBean bankConfig = directDebitConfig
				.getBankDetail(sofDetail.getBankCode());

		FeeUtil feeUtil = new FeeUtil();
		BigDecimal totalFee = feeUtil.calculateFee(quoteRequest.getAmount(),
				bankConfig.getFeeValue(), bankConfig.getFeeType(),
				bankConfig.getMinTotalFee(), bankConfig.getMaxTotalFee());

		// --- Generate Order ID ---//
		TopUpQuote topupQuote = new TopUpQuote();
		String orderID = UUID.randomUUID().toString();
		topupQuote.setID(orderID);
		topupQuote.setAccessTokenID(accessTokenID);
		topupQuote.setUsername(accessToken.getUsername());
		topupQuote.setAmount(amount);
		topupQuote.setTopUpFee(totalFee);
		topupQuote.setSourceOfFund(sofDetail);

		orderRepo.saveTopUpQuote(topupQuote);

		return topupQuote;
	}

	private StandardMoneyResponse verifyTopupEwallet(BigDecimal amount,
			Integer channelID, String sofType) {
		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();
		request.setAmount(amount);
		request.setChannelId(channelID);
		request.setSourceType(sofType);

		return ewalletProxy.verifyAddMoney(request);
	}

	private DirectDebit getSourceOfFund(String sourceOfFundID,
			AccessToken accessToken) {
		String truemoneyID = accessToken.getTruemoneyID();
		Integer channelID = accessToken.getChannelID();
		String sessionID = accessToken.getSessionID();

		DirectDebit sofDetail = sofRepo.getUserDirectDebitSourceByID(
				sourceOfFundID, truemoneyID, channelID, sessionID);
		return sofDetail;
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder requestPlaceOrder(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		if (accessToken == null) {
			throw new ServiceInventoryException(
					ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
					"AccessTokenID is expired or not found.");
		}

		logger.debug("retrieve access Token: " + accessToken.toString());

		String otpReferenceCode = otpService.send(accessToken.getMobileno());
		logger.debug("otpReferenceCode : " + otpReferenceCode);

		TopUpOrder topUpOrder = createTopupOrderFromQuote(quoteID,
				otpReferenceCode);

		return topUpOrder;
	}

	private TopUpOrder createTopupOrderFromQuote(String quoteID,
			String otpReferenceCode) {
		TopUpOrder topUpOrder = new TopUpOrder(orderRepo.getTopUpQuote(quoteID));
		topUpOrder.setStatus(TopUpStatus.AWAITING_CONFIRM);
		topUpOrder.setOtpReferenceCode(otpReferenceCode);
		orderRepo.saveTopUpOrder(topUpOrder);
		return topUpOrder;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken) throws ServiceInventoryException {
		logger.debug("processing " + topUpOrderId);
		TopUpOrder topUpOrder = orderRepo.getTopUpOrder(topUpOrderId);
		AccessToken accessTokenObj = accessTokenRepo.getAccessToken(accessToken);		
		String otpString = otpService.getOTPString(accessTokenObj.getMobileno());
		
		if(otp.getOtpString().equals(otpString)){
			throw new ServiceInventoryException( ServiceInventoryException.Code.OTP_NOT_MATCH,
					"Invalide OTP.");
		}

		String localChecksum = EncryptUtil.buildHmacSignature(accessToken, topUpOrder.toString() + accessToken);
		if (otp.getChecksum().equals(localChecksum)) {
			
			topUpOrder.setStatus(TopUpStatus.PROCESSING);
			orderRepo.saveTopUpOrder(topUpOrder);
			asyncService.topUpUtibaEwallet(topUpOrder, accessTokenObj);
		} else {
			throw new ServiceInventoryException( ServiceInventoryException.Code.INVALID_CHECKSUM, 
					"Invalide Checksum.");
		}

		return topUpOrder;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String topUpOrderID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public OrderRepository getOrderRepo() {
		return orderRepo;
	}

	public void setOrderRepo(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

	public SourceOfFundRepository getSofRepo() {
		return sofRepo;
	}

	public void setSofRepo(SourceOfFundRepository sofRepo) {
		this.sofRepo = sofRepo;
	}

	public AsyncService getAsyncService() {
		return asyncService;
	}

	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	public AccessTokenRepository getAccessTokenRepo() {
		return accessTokenRepo;
	}

	public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setEWalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setDirectDebitConfig(DirectDebitConfig directDebitConfig) {
		this.directDebitConfig = directDebitConfig;
	}

	public void setSourceOfFundRepository(
			SourceOfFundRepository sourceOfFundRepo) {
		this.sofRepo = sourceOfFundRepo;
	}

	public void setOrderRepository(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

}
