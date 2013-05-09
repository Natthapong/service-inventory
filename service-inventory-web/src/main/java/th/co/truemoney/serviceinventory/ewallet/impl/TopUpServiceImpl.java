package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TopUpServiceImpl implements TopUpService {

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	private EnhancedDirectDebitSourceOfFundService directDebitSourceService;

	@Autowired
	private OTPService otpService;

	@Autowired
	private AsyncTopUpEwalletProcessor asyncTopUpProcessor;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;


	@Override
	public TopUpQuote createAndVerifyTopUpQuote(String sourceOfFundID, BigDecimal amount, String accessTokenID) {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		DirectDebit directDebitSource = directDebitSourceService.getUserDirectDebitSource(sourceOfFundID, accessTokenID);

		validateToppingUpValue(amount, directDebitSource);

		legacyFacade.fromChannel(accessToken.getChannelID())
					.topUp(amount)
					.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
					.usingSourceOFFund(directDebitSource.getSourceOfFundID(), directDebitSource.getSourceOfFundType())
					.verify();

		BigDecimal topUpFee = directDebitSourceService.calculateTopUpFee(amount, directDebitSource);

		TopUpQuote topUpQuote = createTopUpQuote(amount, directDebitSource, topUpFee, accessToken);

		transactionRepo.saveDraftTransaction(topUpQuote, accessTokenID);

		return topUpQuote;
	}

	private TopUpQuote createTopUpQuote(BigDecimal amount, SourceOfFund source, BigDecimal topUpFee, AccessToken accessToken) {
		TopUpQuote topUpQuote = new TopUpQuote();
		String orderID = UUID.randomUUID().toString();
		topUpQuote.setID(orderID);
		topUpQuote.setAccessTokenID(accessToken.getAccessTokenID());
		topUpQuote.setAmount(amount);
		topUpQuote.setTopUpFee(topUpFee);
		topUpQuote.setSourceOfFund(source);
		topUpQuote.setStatus(TopUpQuote.Status.CREATED);

		return topUpQuote;

	}

	private void validateToppingUpValue(BigDecimal amount, DirectDebit sofDetail) {
		BigDecimal minAmount = sofDetail.getMinAmount();
		BigDecimal maxAmount = sofDetail.getMaxAmount();

		if (minAmount != null && amount.compareTo(minAmount) < 0) {
			ServiceInventoryWebException se = new ServiceInventoryWebException(Code.INVALID_AMOUNT_LESS, "amount less than min amount.");
			se.marshallToData(sofDetail);
			throw se;
		}
		if (maxAmount != null && amount.compareTo(maxAmount) > 0) {
			ServiceInventoryWebException se = new ServiceInventoryWebException(Code.INVALID_AMOUNT_MORE, "amount more than max amount.");
			se.marshallToData(sofDetail);
			throw se;
		}
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID)
			throws ServiceInventoryException {
		return transactionRepo.findDraftTransaction(quoteID, accessTokenID, TopUpQuote.class);
	}

	@Override
	public OTP requestOTP(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);
		topUpQuote.setOtpReferenceCode(otp.getReferenceCode());
		topUpQuote.setStatus(TopUpQuote.Status.OTP_SENT);

		transactionRepo.saveDraftTransaction(topUpQuote, accessTokenID);

		return otp;
	}

	@Override
	public TopUpQuote.Status verifyOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryWebException {

		accessTokenRepo.findAccessToken(accessTokenID);
		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);

		otpService.isValidOTP(otp);

		topUpQuote.setStatus(TopUpQuote.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(topUpQuote, accessTokenID);

		return topUpQuote.getStatus();
	}

	@Override
	public Status performTopUp(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);

		if (TopUpQuote.Status.OTP_CONFIRMED != topUpQuote.getStatus()) {
			throw new UnVerifiedOwnerTransactionException();
		}

		TopUpOrder topUpOrder = new TopUpOrder(topUpQuote);
		topUpOrder.setStatus(TopUpOrder.Status.VERIFIED);
		transactionRepo.saveTransaction(topUpOrder, accessTokenID);

		asyncTopUpProcessor.topUpUtibaEwallet(topUpOrder, accessToken);

		return topUpOrder.getStatus();
	}


	@Override
	public TopUpOrder.Status getTopUpProcessingStatus(String orderID, String accessTokenID) throws ServiceInventoryWebException {
		TopUpOrder topUpOrder = getTopUpOrderResults(orderID, accessTokenID);
		TopUpOrder.Status topUpStatus = topUpOrder.getStatus();
		FailStatus failStatus = topUpOrder.getFailStatus();

		if(topUpStatus == TopUpOrder.Status.FAILED) {
			if (failStatus == FailStatus.BANK_FAILED) {
				throw new ServiceInventoryWebException(Code.CONFIRM_BANK_FAILED,
						"bank confirmation processing fail.");
			} else if (failStatus == FailStatus.UMARKET_FAILED) {
				throw new ServiceInventoryWebException(Code.CONFIRM_UMARKET_FAILED,
						"u-market confirmation processing fail.");
			} else {
				throw new ServiceInventoryWebException(Code.CONFIRM_FAILED,
						"confirmation processing fail.");
			}
		}

		return topUpStatus;
	}

	public TopUpOrder getTopUpOrderResults(String orderID, String accessTokenID) throws ServiceInventoryException {
		return transactionRepo.findTransaction(orderID, accessTokenID, TopUpOrder.class);
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

	public LegacyFacade getLegacyFacade() {
		return legacyFacade;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}

	public AsyncTopUpEwalletProcessor getAsyncTopUpProcessor() {
		return asyncTopUpProcessor;
	}

	public void setAsyncTopUpProcessor(AsyncTopUpEwalletProcessor asyncTopUpProcessor) {
		this.asyncTopUpProcessor = asyncTopUpProcessor;
	}

	public AccessTokenRepository getAccessTokenRepo() {
		return accessTokenRepo;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public TransactionRepository getTransactionRepository() {
		return transactionRepo;
	}

	public void setTransactionRepository(TransactionRepository orderRepo) {
		this.transactionRepo = orderRepo;
	}
}
