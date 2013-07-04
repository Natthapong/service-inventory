package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
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
    
	public ResetPassword verifyResetPassword(Integer channelID, String resetPasswordToken) {
		try {
			VerifyForgotPasswordRequest request = createVerifyForgotPasswordRequest(tmnProfileInitiator, tmnProfilePin, channelID, resetPasswordToken);
			VerifyForgotPasswordResponse response = tmnProfileAdminProxy.verifyForgotPassword(request);
			ResetPassword resetPassword = new ResetPassword();
			resetPassword.setLoginID(response.getLoginId());
			resetPassword.setTruemoneyID(response.getTmnId());
			resetPassword.setMobileNumber("");			
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
    	confirmForgotPasswordRequest.setChannelId(channelID);
    	confirmForgotPasswordRequest.setLoginId(loginID);
    	confirmForgotPasswordRequest.setNewPin(newPassword);
    	confirmForgotPasswordRequest.setForgotToken(resetPasswordToken);

		String encryptedPin = encryptSHA1(tmnProfileInitiator, tmnProfilePin);

        AdminSecurityContext adminSecurityContext = new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
        confirmForgotPasswordRequest.setAdminSecurityContext(adminSecurityContext);
        return confirmForgotPasswordRequest;
	}

	private VerifyForgotPasswordRequest createVerifyForgotPasswordRequest(String tmnProfileInitiator, String tmnProfilePin, Integer channelID, String resetPasswordToken) {
    	VerifyForgotPasswordRequest verifyForgotPasswordRequest = new VerifyForgotPasswordRequest();
    	verifyForgotPasswordRequest.setChannelId(channelID);
    	verifyForgotPasswordRequest.setForgotToken(resetPasswordToken);

		String encryptedPin = encryptSHA1(tmnProfileInitiator, tmnProfilePin);

        AdminSecurityContext adminSecurityContext = new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
        verifyForgotPasswordRequest.setAdminSecurityContext(adminSecurityContext);
        return verifyForgotPasswordRequest;
    }

	private String encryptSHA1(String tmnProfileInitiator, String tmnProfilePin) {
		String initiator = tmnProfileInitiator != null ? tmnProfileInitiator.toLowerCase() : "";
		return HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase();
	}
	
	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {

		private static final long serialVersionUID = 7194368344939582450L;

		public UnknownSystemTransactionFailException(EwalletException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}
}
