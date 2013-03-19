package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.util.EncryptUtil;
import th.co.truemoney.serviceinventory.util.FeeUtil;

public class TopUpServiceImpl implements TopUpService {

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

    private static final Logger logger = LoggerFactory.getLogger(TopUpServiceImpl.class);
    
	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundId, QuoteRequest quoteRequest, String accessTokenID) {
		
		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		
		if (accessToken == null)
			throw new ServiceInventoryException("90001", "AccessToken not found.");

		// --- get SOF List ---//
		DirectDebit sofDetail = getSourceOfFund(sourceOfFundId, accessToken);
		
		// --- Connect to Ewallet Client to verify amount on this ewallet-account ---//
		try {
			StandardMoneyResponse verifyResponse = verifyTopupEwallet(quoteRequest.getAmount(), accessToken.getChannelId(), sofDetail.getSourceOfFundType());
			
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(), "verify add money fail.", e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException( Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), e.getMessage(), e.getNamespace());
		}

		// --- calculate total FEE ---//
		DirectDebitConfigBean bankConfig = directDebitConfig
				.getBankDetail(sofDetail.getBankCode());

		FeeUtil feeUtil = new FeeUtil();
		BigDecimal totalFee = feeUtil.calculateFee(
				quoteRequest.getAmount(), bankConfig.getFeeValue(),
				bankConfig.getFeeType(), bankConfig.getMinTotalFee(),
				bankConfig.getMaxTotalFee());


		// --- Generate Order ID ---//
		TopUpQuote topupQuote = new TopUpQuote();
		String orderID = UUID.randomUUID().toString();
		topupQuote.setId(orderID);
		topupQuote.setAccessToken(accessTokenID);
		topupQuote.setTopUpFee(totalFee);

		orderRepo.saveTopUpQuote(topupQuote);

		return topupQuote;
	}

	private StandardMoneyResponse verifyTopupEwallet(BigDecimal amount, Integer channelId, String sofType) {
		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();
		request.setAmount(amount);
		request.setChannelId(channelId);
		request.setSourceType(sofType);
		
		return ewalletProxy.verifyAddMoney(request);
	}

	private DirectDebit getSourceOfFund(String sourceOfFundId, AccessToken accessToken) {
		String truemoneyId = accessToken.getTruemoneyId();
		Integer channelId = accessToken.getChannelId();
		String sessionId = accessToken.getSessionId();
		
		DirectDebit sofDetail = sofRepo.getUserDirectDebitSourceById(sourceOfFundId, truemoneyId, channelId, sessionId);
		return sofDetail;
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String quoteId, String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken) {
		logger.debug("processing "+topUpOrderId);
		TopUpOrder topUpOrder = orderRepo.getTopUpOrder(topUpOrderId);		
		
		String localChecksum = EncryptUtil.buildHmacSignature(accessToken, topUpOrder.toString()+accessToken);
		if(otp.getChecksum().equals(localChecksum)) {
			AccessToken accessTokenObj = accessTokenRepo.getAccessToken(accessToken);
			topUpOrder.setStatus(TopUpStatus.PROCESSING);
			orderRepo.saveTopUpOrder(topUpOrder);
			asyncService.topUpUtibaEwallet(topUpOrder, accessTokenObj);			
		}
				
		return topUpOrder;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String topUpOrderId,
			String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderId,
			String accessToken) {
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

}
