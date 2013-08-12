package th.co.truemoney.serviceinventory.authen.impl;

import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.AppleUserConfigException;
import th.co.truemoney.serviceinventory.exception.OTPAlreadyConfirmedException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class TransactionAuthenServiceImpl implements TransactionAuthenService {

    private static final String WORKFLOW_STATUS = "workflowStatus";

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private OTPService otpService;
    
    @Autowired
    private AppleUserMap appleUserMap;

    @Override
    public OTP requestOTP(String draftID, String accessTokenID)
	    throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
	
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);
	
		if (draftTransaction.getStatus() == Status.OTP_CONFIRMED) {
		    throw new OTPAlreadyConfirmedException();
		}
		
		OTP otp = skipOTPForAppleUser(accessToken);
		
		draftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		draftTransaction.setStatus(DraftTransaction.Status.OTP_SENT);
	
		transactionRepo.saveDraftTransaction(draftTransaction, accessTokenID);
	
		MDC.put(WORKFLOW_STATUS, draftTransaction.getStatus());
	
		return otp;

    }

    @Override
    public Status verifyOTP(String draftID, OTP otp, String accessTokenID)
	    throws ServiceInventoryException {

		accessTokenRepo.findAccessToken(accessTokenID);
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(draftID, accessTokenID, DraftTransaction.class);
	
		if (draftTransaction.getStatus() == Status.OTP_CONFIRMED) {
		    throw new OTPAlreadyConfirmedException();
		}
	
		otpService.isValidOTP(otp);
		 
		draftTransaction.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(draftTransaction, accessTokenID);
	
		return draftTransaction.getStatus();
    }
    
	public OTP skipOTPForAppleUser(AccessToken accessToken) {
		
		OTP otp = null;
		if (appleUserMap.getAppleUsers() != null && appleUserMap.getAppleUsers().size() > 0) {
			AppleUser user = appleUserMap.getAppleUsers().get(accessToken.getTruemoneyID());	
			if (user != null && user.getOtpString() != null) {
				otp = otpService.saveOtpString(accessToken.getMobileNumber(), user.getOtpString());
			} else {
				otp = otpService.send(accessToken.getMobileNumber());
			}
		} else {
			throw new AppleUserConfigException();
		}
		return otp;
		
	}

}
