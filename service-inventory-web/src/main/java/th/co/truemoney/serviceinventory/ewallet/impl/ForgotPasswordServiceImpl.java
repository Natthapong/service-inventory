package th.co.truemoney.serviceinventory.ewallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class ForgotPasswordServiceImpl implements ForgotPasswordService {
	
	private static Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	
	@Autowired
	private LegacyFacade legacyFacade;
	
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;
    
    @Autowired
    private OTPService otpService;
    
	@Override
	public ForgotPassword requestForgotPassword(ForgotPassword request)
			throws ServiceInventoryException {
		logger.debug("request forgot password for " + request.getUsername());
		CreateForgotPasswordRequest createForgotPasswordRequest = new CreateForgotPasswordRequest();
		createForgotPasswordRequest.setChannelId(request.getChannelID());
		createForgotPasswordRequest.setLoginId(request.getUsername());
		createForgotPasswordRequest.setThaiId(request.getIdcard());
		
		tmnProfileAdminProxy.createForgotPassword(createForgotPasswordRequest);
		
		return request; //just echo back something
	}

	@Override
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request)	throws ServiceInventoryException {
		
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
		
		otpService.isValidOTP(verifyResetPassword.getOtp());
		
		ResetPassword resetPassword = forgotPasswordRepository.findResetPassword(verifyResetPassword.getResetPasswordID());
				
		legacyFacade.forgotPassword()
				   .fromChannel(channelID)
				   .withToken(resetPassword.getToken())
				   .withNewPassword(resetPassword.getLoginID(), resetPassword.getNewPassword())
				   .confirmResetPassword();
		
		return resetPassword.getToken();
		
	}

	public void setForgotPasswordRepository(ForgotPasswordRepository forgotPasswordRepository) {
		this.forgotPasswordRepository = forgotPasswordRepository;
	}

}
