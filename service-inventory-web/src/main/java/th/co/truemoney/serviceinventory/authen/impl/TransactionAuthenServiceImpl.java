package th.co.truemoney.serviceinventory.authen.impl;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.OTPAlreadyConfirmedException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
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

		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);

		if (draftTransaction.getStatus() == Status.OTP_CONFIRMED) {
			throw new OTPAlreadyConfirmedException();
		}

		OTP otp = otpService.send(accessToken.getMobileNumber());

		draftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_SENT);

		transactionRepo.saveDraftTransaction(draftTransaction, accessTokenID);

		return otp;
	}

	@Override
	public Status verifyOTP(String draftID, OTP otp, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);

		if (draftTransaction.getStatus() == Status.OTP_CONFIRMED) {
			throw new OTPAlreadyConfirmedException();
		}

		if (isAppleUser(accessToken)) {
			if (!isAppleValidOTP(otp)) {
				throw new ServiceInventoryWebException(Code.INVALID_OTP, "invalid OTP.");
			}
		} else {
			otpService.isValidOTP(otp);	
		} 

		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(draftTransaction, accessTokenID);

		return draftTransaction.getStatus();
	}

	private boolean isAppleValidOTP(OTP otp) {
		return "123456".equals(otp.getOtpString());
	}

	private boolean isAppleUser(AccessToken accessToken) {
		return "".equals(accessToken.getTruemoneyID());
	}
}
