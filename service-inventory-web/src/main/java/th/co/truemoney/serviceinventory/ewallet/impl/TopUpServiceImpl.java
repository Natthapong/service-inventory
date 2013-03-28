package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrderStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TopUpServiceImpl implements TopUpService {

	private static final Logger logger = LoggerFactory.getLogger(TopUpServiceImpl.class);

	@Autowired
	private EnhancedDirectDebitSourceOfFundService directDebitSourceService;

	@Autowired
	private OTPService otpService;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository orderRepo;


	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID,
			BigDecimal amount, String accessTokenID) {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		DirectDebit directDebitSource = directDebitSourceService.getUserDirectDebitSource(sourceOfFundID, accessTokenID);

		validateToppingUpValue(amount, directDebitSource);
		verifyToppingUpCapability(amount, directDebitSource, accessToken);

		BigDecimal topUpFee = directDebitSourceService.calculateTopUpFee(amount, directDebitSource);

		TopUpQuote topUpQuote = new TopUpQuote();
		String orderID = UUID.randomUUID().toString();
		topUpQuote.setID(orderID);
		topUpQuote.setAccessTokenID(accessTokenID);
		topUpQuote.setUsername(accessToken.getUsername());
		topUpQuote.setAmount(amount);
		topUpQuote.setTopUpFee(topUpFee);
		topUpQuote.setSourceOfFund(directDebitSource);
		topUpQuote.setStatus(DraftTransaction.Status.CREATED);

		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

		return topUpQuote;
	}

	private void validateToppingUpValue(BigDecimal amount, DirectDebit sofDetail) {
		BigDecimal minAmount = sofDetail.getMinAmount();
		BigDecimal maxAmount = sofDetail.getMaxAmount();

		if (amount.compareTo(minAmount) < 0) {
			ServiceInventoryException se = new ServiceInventoryException(ServiceInventoryException.Code.INVALID_AMOUNT_LESS, "amount less than min amount.");
			se.marshallToData(sofDetail);
			throw se;
		}
		if (amount.compareTo(maxAmount) > 0) {
			ServiceInventoryException se = new ServiceInventoryException(ServiceInventoryException.Code.INVALID_AMOUNT_MORE, "amount more than max amount.");
			se.marshallToData(sofDetail);
			throw se;
		}
	}

	private void verifyToppingUpCapability(BigDecimal amount, SourceOfFund sof, AccessToken accessToken) {
		try {

			VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();

			request.setAmount(amount);
			request.setSourceId(sof.getSourceOfFundID());
			request.setSourceType(sof.getSourceOfFundType());
			request.setChannelId(accessToken.getChannelID());
			request.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));

			ewalletProxy.verifyAddMoney(request);

		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(), "verify add money fail.", e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), e.getMessage(), e.getNamespace());
		}
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		return orderRepo.getTopUpEwalletDraftTransaction(quoteID, accessTokenID);
	}

	@Override
	public OTP sendOTPConfirm(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);
		topUpQuote.setOtpReferenceCode(otp.getReferenceCode());
		topUpQuote.setStatus(DraftTransaction.Status.OTP_SENT);

		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

		return otp;
	}

	@Override
	public DraftTransaction.Status confirmOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);

		if(!otpService.isValidOTP(otp)){
			throw new ServiceInventoryException( ServiceInventoryException.Code.OTP_NOT_MATCH, "Invalide OTP.");
		}

		topUpQuote.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		orderRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

		TopUpOrder topUpOrder = new TopUpOrder(topUpQuote);
		topUpOrder.setStatus(TopUpOrderStatus.ORDER_VERIFIED);
		orderRepo.saveTopUpEwalletTransaction(topUpOrder, accessTokenID);

		performTopUpMoney(accessToken, topUpOrder);

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
		return orderRepo.getTopUpEwalletTransaction(orderID, accessTokenID);
	}

	private void performTopUpMoney(AccessToken accessToken, TopUpOrder topUpOrder) {
		TopUpQuote quote = topUpOrder.getQuote();

		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(quote.getAmount());
		addMoneyRequest.setChannelId(accessToken.getChannelID());
		addMoneyRequest.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));
		addMoneyRequest.setSourceId(quote.getSourceOfFund().getSourceOfFundID());
		addMoneyRequest.setSourceType(quote.getSourceOfFund().getSourceOfFundType());

		asyncService.topUpUtibaEwallet(topUpOrder, accessToken.getAccessTokenID(), addMoneyRequest);
	}

	public EnhancedDirectDebitSourceOfFundService getDirectDebitSourceService() {
		return directDebitSourceService;
	}

	public void setDirectDebitSourceService(EnhancedDirectDebitSourceOfFundService directDebitSourceService) {
		this.directDebitSourceService = directDebitSourceService;
	}

	public OTPService getOtpService() {
		return otpService;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public AsyncService getAsyncService() {
		return asyncService;
	}

	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public AccessTokenRepository getAccessTokenRepository() {
		return accessTokenRepo;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public TransactionRepository getOrderRepository() {
		return orderRepo;
	}

	public void setOrderRepository(TransactionRepository orderRepo) {
		this.orderRepo = orderRepo;
	}
}
