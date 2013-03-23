package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
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
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.util.FeeUtil;

@Service
public class TopUpServiceImpl implements TopUpService {

	private static final Logger logger = LoggerFactory
			.getLogger(TopUpServiceImpl.class);

	@Autowired
	@Qualifier("accessTokenRedisRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private SourceOfFundRepository sofRepo;

	@Autowired
	private DirectDebitConfig directDebitConfig;

	@Autowired
	@Qualifier("orderRedisRepository")
	private OrderRepository orderRepo;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private OTPService otpService;

	@Override
    @SuppressWarnings("unchecked")
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID,
			QuoteRequest quoteRequest, String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		if (accessToken == null) {
			throw new ServiceInventoryException(
					ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
					"AccessTokenID is expired or not found.");
		}

		// --- get SOF List ---//
		DirectDebit sofDetail = getSourceOfFund(sourceOfFundID, accessToken);

		BigDecimal amount = quoteRequest.getAmount();
		BigDecimal minAmount = sofDetail.getMinAmount();
		BigDecimal maxAmount = sofDetail.getMaxAmount();
		if (amount.compareTo(minAmount) < 0) {
			ServiceInventoryException se = new ServiceInventoryException(
					ServiceInventoryException.Code.INVALID_AMOUNT_LESS,
					"amount less than min amount.");
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> hashMap = mapper.convertValue(sofDetail, HashMap.class);
			se.setData(hashMap);
			throw se; 
		}
		if (amount.compareTo(maxAmount) > 0) {
			ServiceInventoryException se = new ServiceInventoryException(
					ServiceInventoryException.Code.INVALID_AMOUNT_MORE,
					"amount less than max amount.");
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> hashMap = mapper.convertValue(sofDetail, HashMap.class);
			se.setData(hashMap);
			throw se; 
		}

		// --- Connect to Ewallet Client to verify amount on this
		// ewallet-account ---//
		try {
			StandardMoneyResponse verifyResponse = verifyTopupEwallet(
					quoteRequest.getAmount(), 
					accessToken.getChannelID(),
					sofDetail.getSourceOfFundID(),
					sofDetail.getSourceOfFundType(),
					accessToken.getSessionID(),
					accessToken.getTruemoneyID());

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
		
		logger.debug("source id: "+topupQuote.getSourceOfFund().getSourceOfFundID());
		logger.debug("source type: "+topupQuote.getSourceOfFund().getSourceOfFundType());

		orderRepo.saveTopUpQuote(topupQuote);

		return topupQuote;
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
		
		accessTokenRepo.getAccessToken(accessTokenID);

		TopUpQuote topUpQuote = orderRepo.getTopUpQuote(quoteID);
		
		return topUpQuote;
	}

	@Override
	public TopUpOrder requestPlaceOrder(String quoteID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		String otpReferenceCode = otpService.send(accessToken.getMobileno());

		TopUpOrder topUpOrder = createTopupOrderFromQuote(quoteID, otpReferenceCode);

		return topUpOrder;
	}

	private TopUpOrder createTopupOrderFromQuote(String quoteID,
			String otpReferenceCode) {
		TopUpQuote topUpQuote = orderRepo.getTopUpQuote(quoteID);
		logger.debug("topUpQuote: "+topUpQuote.toString());
		TopUpOrder topUpOrder = new TopUpOrder(topUpQuote);
		topUpOrder.setStatus(TopUpStatus.AWAITING_CONFIRM);
		topUpOrder.setOtpReferenceCode(otpReferenceCode);
		orderRepo.saveTopUpOrder(topUpOrder);
		return topUpOrder;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken) throws ServiceInventoryException {
		logger.debug("processing " + topUpOrderId);
		TopUpOrder topUpOrder = orderRepo.getTopUpOrder(topUpOrderId);
		logger.debug(topUpOrder.toString());
		AccessToken accessTokenObj = accessTokenRepo.getAccessToken(accessToken);	
		logger.debug(accessTokenObj.toString());
		String otpString = otpService.getOTPString(accessTokenObj.getMobileno());
		if(!otp.getOtpString().equals(otpString)){
			throw new ServiceInventoryException( ServiceInventoryException.Code.OTP_NOT_MATCH,
					"Invalide OTP.");
		}		
		
		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();			
		addMoneyRequest.setAmount(topUpOrder.getAmount());
		addMoneyRequest.setChannelId(accessTokenObj.getChannelID());		
		addMoneyRequest.setSecurityContext(new SecurityContext(accessTokenObj.getSessionID(), accessTokenObj.getTruemoneyID()));
		addMoneyRequest.setSourceId(topUpOrder.getSourceOfFund().getSourceOfFundID());
		addMoneyRequest.setSourceType(topUpOrder.getSourceOfFund().getSourceOfFundType());
			
		topUpOrder.setStatus(TopUpStatus.PROCESSING);
		orderRepo.saveTopUpOrder(topUpOrder);
		asyncService.topUpUtibaEwallet(topUpOrder, addMoneyRequest);
		logger.debug("processing " + orderRepo.getTopUpOrder(topUpOrder.getID()).getStatus());
		logger.debug("time " + new Date());
		

		return topUpOrder;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String topUpOrderID,
			String accessTokenID) throws ServiceInventoryException {
		TopUpStatus topUpStatus = getTopUpOrderDetails(topUpOrderID, accessTokenID).getStatus();		

		if(topUpStatus == TopUpStatus.BANK_FAILED) {
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_BANK_FAILED, 
					"bank confirmation processing fail.");
		} else if (topUpStatus == TopUpStatus.UMARKET_FAILED) {
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_UMARKET_FAILED, 
					"u-market confirmation processing fail.");
		} else if (topUpStatus == TopUpStatus.FAILED){
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_FAILED, 
					"confirmation processing fail.");
		}		

		return topUpStatus;
	}

	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderID,
			String accessTokenID) throws ServiceInventoryException {
		
		accessTokenRepo.getAccessToken(accessTokenID);

		TopUpOrder topUpOrder = orderRepo.getTopUpOrder(topUpOrderID);
		
		return topUpOrder;
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

	public OrderRepository getOrderRepo() {
		return orderRepo;
	}

	public void setOrderRepository(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

	public OTPService getOrderRepository() {
		return otpService;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

}
