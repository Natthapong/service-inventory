package th.co.truemoney.serviceinventory.authen.impl;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

public class TransactionAuthenServiceImpl implements TransactionAuthenService {

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private OTPService otpService;

	@Override
	public OTP requestOTP(String draftID, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);
		draftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_SENT);

		transactionRepo.saveDraftTransaction(draftTransaction, accessTokenID);

		return otp;
	}

	@Override
	public Status verifyOTP(String draftID, OTP otp, String accessTokenID)
			throws ServiceInventoryException {

		accessTokenRepo.findAccessToken(accessTokenID);
		DraftTransaction topUpMobileDraft = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);

		otpService.isValidOTP(otp);

		topUpMobileDraft.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(topUpMobileDraft, accessTokenID);

		return topUpMobileDraft.getStatus();
	}
}
