package th.co.truemoney.serviceinventory.ewallet.impl;

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
	
	@Autowired
	private LegacyFacade legacyFacade;
	
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;
    
    @Autowired
    private OTPService otpService;
    
	@Override
	public ForgotPassword createForgotPassword(Integer channelID, ForgotPassword request)
			throws ServiceInventoryException {
		legacyFacade.forgotPassword()
					.fromChannel(channelID)
					.withLogin(request.getLoginID())
					.withIdCardNumber(request.getIdCardNumber())
					.createForgotPassword();
		return request;
	}

	@Override
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request) throws ServiceInventoryException {
		ResetPassword resetPassword = legacyFacade.forgotPassword()
						   .fromChannel(channelID)
						   .withToken(request.getToken())
						   .verifyResetPassword();
		resetPassword.setToken(request.getToken());
		
		OTP otp = otpService.send(resetPassword.getMobileNumber());

		forgotPasswordRepository.saveResetPassword(resetPassword.getToken(), resetPassword);
		
		VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
		verifyResetPassword.setOtp(otp);
		verifyResetPassword.setResetPasswordID(resetPassword.getToken());
		
		return verifyResetPassword;
	}

	@Override
	public String verifyOTP(Integer channelID, VerifyResetPassword verifyResetPassword) throws ServiceInventoryException {
		otpService.isValidOTP(verifyResetPassword.getOtp());
		
		ResetPassword resetPassword = forgotPasswordRepository.findResetPassword(verifyResetPassword.getResetPasswordID());
				
		return resetPassword.getToken();
	}

	@Override
	public String confirmResetPassword(Integer channelID, ResetPassword request) throws ServiceInventoryException {
		ResetPassword resetPassword = forgotPasswordRepository.findResetPassword(request.getToken());
				
		legacyFacade.forgotPassword()
				   .fromChannel(channelID)
				   .withToken(resetPassword.getToken())
				   .withNewPassword(resetPassword.getLoginID(), request.getNewPassword())
				   .confirmResetPassword();
		
		return resetPassword.getToken();
	}
	
	@Override
	public VerifyResetPassword resendOTP(Integer channelID, String resetPasswordID) {
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
