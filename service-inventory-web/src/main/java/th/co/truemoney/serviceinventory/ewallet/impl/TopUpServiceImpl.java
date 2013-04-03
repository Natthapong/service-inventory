package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TopUpServiceImpl implements TopUpService {

	@Autowired
	private EnhancedDirectDebitSourceOfFundService directDebitSourceService;

	@Autowired
	private OTPService otpService;

	@Autowired
	private AsyncTopUpEwalletProcessor asyncTopUpProcessor;

	@Autowired
	private BalanceFacade.TopUpBuilder topUpFacade;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;


	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID,
			BigDecimal amount, String accessTokenID) {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		DirectDebit directDebitSource = directDebitSourceService.getUserDirectDebitSource(sourceOfFundID, accessTokenID);

		validateToppingUpValue(amount, directDebitSource);

		topUpFacade.withAmount(amount)
			.usingSourceOfFund(directDebitSource)
			.fromUser(accessToken)
			.verifyTopUp();

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

		transactionRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

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

		return transactionRepo.getTopUpEwalletDraftTransaction(quoteID, accessTokenID);
	}

	@Override
	public OTP sendOTPConfirm(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);
		topUpQuote.setOtpReferenceCode(otp.getReferenceCode());
		topUpQuote.setStatus(DraftTransaction.Status.OTP_SENT);

		transactionRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

		return otp;
	}

	@Override
	public DraftTransaction.Status confirmOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryWebException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		TopUpQuote topUpQuote = getTopUpQuoteDetails(quoteID, accessTokenID);

		otpService.isValidOTP(otp);

		topUpQuote.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		transactionRepo.saveTopUpEwalletDraftTransaction(topUpQuote, accessTokenID);

		TopUpOrder topUpOrder = new TopUpOrder(topUpQuote);
		topUpOrder.setStatus(Transaction.Status.VERIFIED);
		transactionRepo.saveTopUpEwalletTransaction(topUpOrder, accessTokenID);

		performTopUp(topUpOrder, accessToken);

		return topUpQuote.getStatus();
	}

	private void performTopUp(TopUpOrder topUpOrder, AccessToken accessToken) {
		asyncTopUpProcessor.topUpUtibaEwallet(topUpOrder, accessToken);
	}

	@Override
	public Transaction.Status getTopUpProcessingStatus(String orderID, String accessTokenID) throws ServiceInventoryWebException {
		TopUpOrder topUpOrder = getTopUpOrderResults(orderID, accessTokenID);
		Transaction.Status topUpStatus = topUpOrder.getStatus();
		FailStatus failStatus = topUpOrder.getFailStatus();

		if(topUpStatus == Transaction.Status.FAILED) {
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
		return transactionRepo.getTopUpEwalletTransaction(orderID, accessTokenID);
	}

	public EnhancedDirectDebitSourceOfFundService getDirectDebitSourceService() {
		return directDebitSourceService;
	}

	public void setDirectDebitSourceService(EnhancedDirectDebitSourceOfFundService directDebitSourceService) {
		this.directDebitSourceService = directDebitSourceService;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setTopUpFacadeBuilder(BalanceFacade.TopUpBuilder topUpFacadeBuilder) {
		this.topUpFacade = topUpFacadeBuilder;
	}

	public void setAsyncTopUpProcessor(AsyncTopUpEwalletProcessor asyncTopUpProcessor) {
		this.asyncTopUpProcessor = asyncTopUpProcessor;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setOrderRepository(TransactionRepository orderRepo) {
		this.transactionRepo = orderRepo;
	}
}
