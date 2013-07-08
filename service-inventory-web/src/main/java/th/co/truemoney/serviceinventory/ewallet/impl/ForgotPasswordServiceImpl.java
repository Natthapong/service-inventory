package th.co.truemoney.serviceinventory.ewallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class ForgotPasswordServiceImpl implements ForgotPasswordService {
	
	private static Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);
	
	@Autowired
	private LegacyFacade legacyFacade;
	
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;
    
    @Autowired
    private OTPService otpService;
    
	@Override
	public ForgotPassword createForgotPassword(Integer channelID, ForgotPassword request)
			throws ServiceInventoryException {
		logger.debug("================= createForgotPassword ==================");
		legacyFacade.forgotPassword()
					.fromChannel(channelID)
					.withLogin(request.getLoginID())
					.withIdCardNumber(request.getIdCardNumber())
					.createForgotPassword();
		return request;
	}

	@Override
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request)	throws ServiceInventoryException {
		logger.debug("================= verifyResetPassword ==================");
		ResetPassword resetPassword = legacyFacade.forgotPassword()
						   .fromChannel(channelID)
						   .withToken(request.getToken())
						   .verifyResetPassword();
		
		OTP otp = otpService.send(resetPassword.getMobileNumber());
		
		forgotPasswordRepository.saveResetPassword(resetPassword.getToken(), resetPassword);
		
		VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
		verifyResetPassword.setOtp(otp);
		verifyResetPassword.setResetPasswordID(resetPassword.getToken());
		
		return verifyResetPassword;
	}

	@Override
	public String confirmResetPassword(Integer channelID, VerifyResetPassword verifyResetPassword) throws ServiceInventoryException {
		logger.debug("================= confirmResetPassword ==================");
		otpService.isValidOTP(verifyResetPassword.getOtp());
		
		ResetPassword resetPassword = forgotPasswordRepository.findResetPassword(verifyResetPassword.getResetPasswordID());
				
		legacyFacade.forgotPassword()
				   .fromChannel(channelID)
				   .withToken(resetPassword.getToken())
				   .withNewPassword(resetPassword.getLoginID(), resetPassword.getNewPassword())
				   .confirmResetPassword();
		
		return resetPassword.getToken();
		
	}
	
	@Override
	public VerifyResetPassword resendOTP(Integer channelID, String resetPasswordID) {
		logger.debug("================= resendOTP ==================");
		ResetPassword resetPassword = forgotPasswordRepository.findResetPassword(resetPasswordID);
			
		OTP otp = otpService.send(resetPassword.getMobileNumber());
		
		forgotPasswordRepository.saveResetPassword(resetPassword.getToken(), resetPassword);
		
		VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
		verifyResetPassword.setOtp(otp);
		verifyResetPassword.setResetPasswordID(resetPassword.getToken());
		
		return verifyResetPassword;
	}

	public void setForgotPasswordRepository(ForgotPasswordRepository forgotPasswordRepository) {
		this.forgotPasswordRepository = forgotPasswordRepository;
	}

}
