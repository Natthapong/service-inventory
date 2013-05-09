package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Service
public class TopUpMobileServiceImpl implements TopUpMobileService {

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private OTPService otpService;

	@Autowired
	AsyncTopUpMobileProcessor asyncTopUpMobileProcessor;

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	@Override
	public TopUpMobileDraft verifyAndCreateTopUpMobileDraft(
			String targetMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = accessToken.getClientCredential();

		//verify topup mobile
		TopUpMobile topUpMobile = legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber(targetMobileNumber)
				.usingSourceOfFund("EW")
				.withAmount(amount)
				.verifyTopUpAirtime();

		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft(UUID.randomUUID().toString(), topUpMobile, topUpMobile.getAmount(), topUpMobile.getID(), Status.CREATED);
		transactionRepo.saveDraftTransaction(topUpMobileDraft, accessTokenID);
		return topUpMobileDraft;
	}

	@Override
	public TopUpMobileDraft getTopUpMobileDraftDetail(String draftID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		TopUpMobileDraft topUpMobileDraft = transactionRepo.findDraftTransaction(draftID, accessToken.getAccessTokenID(), TopUpMobileDraft.class);

		return topUpMobileDraft;
	}

	@Override
	public OTP requestOTP(String draftID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		TopUpMobileDraft topUpMobileDraft = transactionRepo.findDraftTransaction(draftID, accessTokenID, TopUpMobileDraft.class);
		topUpMobileDraft.setOtpReferenceCode(otp.getReferenceCode());
		topUpMobileDraft.setStatus(TopUpMobileDraft.Status.OTP_SENT);

		transactionRepo.saveDraftTransaction(topUpMobileDraft, accessTokenID);

		return otp;
	}

	@Override
	public Status verifyOTP(String draftID, OTP otp, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		TopUpMobileDraft topUpMobileDraft = getTopUpMobileDraftDetail(draftID, accessTokenID);

		otpService.isValidOTP(otp);

		topUpMobileDraft.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(topUpMobileDraft, accessTokenID);

		TopUpMobileTransaction topUpMobileTransaction = new TopUpMobileTransaction(topUpMobileDraft);
		topUpMobileTransaction.setStatus(TopUpMobileTransaction.Status.VERIFIED);
		transactionRepo.saveTransaction(topUpMobileTransaction, accessTokenID);

		performTopUpMobile(topUpMobileTransaction, accessToken);

		return topUpMobileDraft.getStatus();
	}

	@Override
	public Transaction.Status performTopUpMobile(
			String draftID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		TopUpMobileDraft topUpMobileDraft = getTopUpMobileDraftDetail(draftID, accessTokenID);

		if (TopUpMobileDraft.Status.OTP_CONFIRMED != topUpMobileDraft.getStatus()) {
			throw new UnVerifiedOwnerTransactionException();
		}

		TopUpMobileTransaction topUpMobileTransaction = new TopUpMobileTransaction(topUpMobileDraft);
		topUpMobileTransaction.setStatus(TopUpMobileTransaction.Status.VERIFIED);
		transactionRepo.saveTransaction(topUpMobileTransaction, accessTokenID);

		performTopUpMobile(topUpMobileTransaction, accessToken);

		return topUpMobileTransaction.getStatus();
	}

	private void performTopUpMobile(TopUpMobileTransaction topUpMobileTransaction, AccessToken accessToken) {
		asyncTopUpMobileProcessor.topUpMobile(topUpMobileTransaction, accessToken);
	}

	@Override
	public TopUpMobileTransaction.Status getTopUpMobileStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		TopUpMobileTransaction topUpMobileTransaction = getTopUpMobileResult(transactionID, accessTokenID);
		return topUpMobileTransaction.getStatus();
	}

	@Override
	public TopUpMobileTransaction getTopUpMobileResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		return transactionRepo.findTransaction(transactionID, accessTokenID, TopUpMobileTransaction.class);
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}
}
