package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrderStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuoteStatus;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.util.FeeUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TopUpServiceImpl implements TopUpService {

	private static final Logger logger = LoggerFactory.getLogger(TopUpServiceImpl.class);

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private SourceOfFundRepository sofRepo;

	@Autowired
	private DirectDebitConfig directDebitConfig;

	@Autowired
	private TransactionRepository orderRepo;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private OTPService otpService;

	@Override
	@SuppressWarnings("unchecked")
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID,
			BigDecimal amount, String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = getAccessTokenByID(accessTokenID);

		// --- get SOF List ---//
		DirectDebit sofDetail = getSourceOfFundById(sourceOfFundID, accessToken);

		BigDecimal minAmount = sofDetail.getMinAmount();
		BigDecimal maxAmount = sofDetail.getMaxAmount();

		if (amount.compareTo(minAmount) < 0) {
			ServiceInventoryException se = new ServiceInventoryException(ServiceInventoryException.Code.INVALID_AMOUNT_LESS, "amount less than min amount.");
			ObjectMapper mapper = new ObjectMapper();

			Map<String, Object> hashMap = mapper.convertValue(sofDetail, HashMap.class);
			se.setData(hashMap);
			throw se;
		}
		if (amount.compareTo(maxAmount) > 0) {
			ServiceInventoryException se = new ServiceInventoryException(ServiceInventoryException.Code.INVALID_AMOUNT_MORE, "amount more than max amount.");
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> hashMap = mapper.convertValue(sofDetail, HashMap.class);
			se.setData(hashMap);
			throw se;
		}

		// --- Connect to Ewallet Client to verify amount on this
		// ewallet-account ---//
		try {
			StandardMoneyResponse verifyResponse = verifyTopupEwallet(
					amount,
					accessToken.getChannelID(),
					sofDetail.getSourceOfFundID(),
					sofDetail.getSourceOfFundType(),
					accessToken.getSessionID(),
					accessToken.getTruemoneyID());

		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(), "verify add money fail.", e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(
					Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
					e.getMessage(), e.getNamespace());
		}

		// --- calculate total FEE ---//
		DirectDebitConfigBean bankConfig = directDebitConfig
				.getBankDetail(sofDetail.getBankCode());

		FeeUtil feeUtil = new FeeUtil();
		BigDecimal totalFee = feeUtil.calculateFee(amount,
				bankConfig.getFeeValue(), bankConfig.getFeeType(),
				bankConfig.getMinTotalFee(), bankConfig.getMaxTotalFee());

		// --- Generate Order ID ---//
		TopUpQuote topUpQuote = new TopUpQuote();
		String orderID = UUID.randomUUID().toString();
		topUpQuote.setID(orderID);
		topUpQuote.setAccessTokenID(accessTokenID);
		topUpQuote.setUsername(accessToken.getUsername());
		topUpQuote.setAmount(amount);
		topUpQuote.setTopUpFee(totalFee);
		topUpQuote.setSourceOfFund(sofDetail);
		topUpQuote.setStatus(TopUpQuoteStatus.CREATED);

		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote);

		return topUpQuote;
	}

	private StandardMoneyResponse verifyTopupEwallet(BigDecimal amount,
			Integer channelID, String sourceOfFundID, String sofType, String sessionID, String truemoneyID) {
		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();
		request.setAmount(amount);
		request.setChannelId(channelID);
		request.setSourceId(sourceOfFundID);
		request.setSourceType(sofType);
		SecurityContext securityContext = new SecurityContext(sessionID, truemoneyID);
		request.setSecurityContext(securityContext);
		return ewalletProxy.verifyAddMoney(request);
	}

	private DirectDebit getSourceOfFundById(String sourceOfFundID,
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

		AccessToken accessToken = getAccessTokenByID(accessTokenID);

		TopUpQuote topUpQuote = orderRepo.getTopUpEwalletDraftTransaction(quoteID);

		if (topUpQuote == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_QUOTE_NOT_FOUND, "quote not found");
		}

		if (!accessToken.getAccessTokenID().equals(topUpQuote.getAccessTokenID())) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_QUOTE_NOT_FOUND, "quote not found");
		}

		return topUpQuote;
	}

	@Override
	public OTP sendOTPConfirm(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);
		topUpQuote.setOtpReferenceCode(otp.getReferenceCode());
		topUpQuote.setStatus(TopUpQuoteStatus.OTP_SENT);

		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote);

		return otp;

	}


	@Override
	public TopUpQuoteStatus confirmOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);

		if(!otpService.isValidOTP(otp)){
			throw new ServiceInventoryException( ServiceInventoryException.Code.OTP_NOT_MATCH, "Invalide OTP.");
		}

		topUpQuote.setStatus(TopUpQuoteStatus.OTP_CONFIRMED);
		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote);

		TopUpOrder topUpOrder = new TopUpOrder(topUpQuote);
		topUpOrder.setStatus(TopUpOrderStatus.ORDER_VERIFIED);
		orderRepo.saveTopUpEwalletTransaction(topUpOrder);

		performTopUpMoney(accessToken, topUpOrder);

		logger.debug("time " + new Date());

		return topUpQuote.getStatus();
	}

	@Override
	public TopUpOrderStatus getTopUpProcessingStatus(String orderID, String accessTokenID) throws ServiceInventoryException {
		TopUpOrderStatus topUpStatus = getTopUpOrderResults(orderID, accessTokenID).getStatus();

		if(topUpStatus == TopUpOrderStatus.BANK_FAILED) {
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_BANK_FAILED,
					"bank confirmation processing fail.");
		} else if (topUpStatus == TopUpOrderStatus.UMARKET_FAILED) {
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_UMARKET_FAILED,
					"u-market confirmation processing fail.");
		} else if (topUpStatus == TopUpOrderStatus.FAILED){
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_FAILED,
					"confirmation processing fail.");
		}

		return topUpStatus;
	}

	public TopUpOrder getTopUpOrderResults(String orderID, String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = getAccessTokenByID(accessTokenID);

		TopUpOrder topUpOrder = orderRepo.getTopUpEwalletTransaction(orderID);

		if (topUpOrder == null || !topUpOrder.getQuote().getAccessTokenID().equals(accessToken.getAccessTokenID())) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_QUOTE_NOT_FOUND, "quote not found");
		}

		return topUpOrder;
	}

	private AccessToken getAccessTokenByID(String accessTokenID) {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		if (accessToken == null) {
			throw new ServiceInventoryException(
					ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
					"AccessTokenID is expired or not found.");
		}

		return accessToken;
	}

	private void performTopUpMoney(AccessToken accessToken, TopUpOrder topUpOrder) {
		TopUpQuote quote = topUpOrder.getQuote();

		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(quote.getAmount());
		addMoneyRequest.setChannelId(accessToken.getChannelID());
		addMoneyRequest.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));
		addMoneyRequest.setSourceId(quote.getSourceOfFund().getSourceOfFundID());
		addMoneyRequest.setSourceType(quote.getSourceOfFund().getSourceOfFundType());

		asyncService.topUpUtibaEwallet(topUpOrder, addMoneyRequest);
	}

	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
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

	public TransactionRepository getOrderRepo() {
		return orderRepo;
	}

	public void setOrderRepository(TransactionRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

	public OTPService getOrderRepository() {
		return otpService;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

}
