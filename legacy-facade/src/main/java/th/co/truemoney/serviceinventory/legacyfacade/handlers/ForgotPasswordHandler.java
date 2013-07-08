package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ForgotPasswordHandler {

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	
	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;
	
	public void createForgotpassword(Integer channelID, String loginID, String idCardNumber) { 
		CreateForgotPasswordRequest createForgotPasswordRequest = createForgotPasswordRequest(channelID, loginID, idCardNumber);
		tmnProfileAdminProxy.createForgotPassword(createForgotPasswordRequest);
	}
	
	public ResetPassword verifyResetPassword(Integer channelID, String resetPasswordToken) {
		try {
			VerifyForgotPasswordRequest request = createVerifyForgotPasswordRequest(tmnProfileInitiator, tmnProfilePin, channelID, resetPasswordToken);
			VerifyForgotPasswordResponse response = tmnProfileAdminProxy.verifyForgotPassword(request);
			ResetPassword resetPassword = new ResetPassword();
			resetPassword.setLoginID(response.getLoginId());
			resetPassword.setTruemoneyID(response.getTmnId());
			resetPassword.setMobileNumber(response.getMobile());
			return resetPassword;
		} catch (FailResultCodeException ex) {
			throw new UnknownSystemTransactionFailException(ex);
	    }
	}

	public void confirmResetPassword(Integer channelID, String newPassword, String loginID, String resetPasswordToken) {
		try {
			ConfirmForgotPasswordRequest request = createConfirmForgotPasswordRequest(tmnProfileInitiator, tmnProfilePin, channelID, newPassword, loginID, resetPasswordToken);
			tmnProfileAdminProxy.confirmForgotPassword(request);
		} catch (FailResultCodeException ex) {
			throw new UnknownSystemTransactionFailException(ex);
	    }
	}

    private ConfirmForgotPasswordRequest createConfirmForgotPasswordRequest(String tmnProfileInitiator, String tmnProfilePin, Integer channelID, String newPassword, String loginID, String resetPasswordToken) {
    	ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
    	confirmForgotPasswordRequest.setAdminSecurityContext(createSecurityContext());
    	confirmForgotPasswordRequest.setForgotToken(resetPasswordToken);
    	confirmForgotPasswordRequest.setChannelId(channelID);
    	confirmForgotPasswordRequest.setNewPin(newPassword);
    	confirmForgotPasswordRequest.setLoginId(loginID);
        return confirmForgotPasswordRequest;
	}

	private VerifyForgotPasswordRequest createVerifyForgotPasswordRequest(String tmnProfileInitiator, String tmnProfilePin, Integer channelID, String resetPasswordToken) {
    	VerifyForgotPasswordRequest verifyForgotPasswordRequest = new VerifyForgotPasswordRequest();
    	verifyForgotPasswordRequest.setForgotToken(resetPasswordToken);
    	verifyForgotPasswordRequest.setAdminSecurityContext(createSecurityContext());
    	verifyForgotPasswordRequest.setChannelId(channelID);
        return verifyForgotPasswordRequest;
    }
	
	private CreateForgotPasswordRequest createForgotPasswordRequest(Integer channelID, String loginID, String idCardNumber) {
		CreateForgotPasswordRequest createForgotPasswordRequest = new CreateForgotPasswordRequest();
		createForgotPasswordRequest.setAdminSecurityContext(createSecurityContext());
		createForgotPasswordRequest.setChannelId(channelID);
		createForgotPasswordRequest.setThaiId(idCardNumber);
		createForgotPasswordRequest.setLoginId(loginID);
        return createForgotPasswordRequest;
	}
	
	private String encryptSHA1(String tmnProfileInitiator, String tmnProfilePin) {
		String initiator = tmnProfileInitiator != null ? tmnProfileInitiator.toLowerCase() : "";
		return HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase();
	}
	
	private AdminSecurityContext createSecurityContext() {
		String encryptedPin = encryptSHA1(tmnProfileInitiator, tmnProfilePin);
        return new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
	}
	
	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {

		private static final long serialVersionUID = 7194368344939582450L;

		public UnknownSystemTransactionFailException(EwalletException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}	
}
