package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;

public class ForgotPasswordHandler {

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	
	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;
	
	public void createForgotpassword(Integer channelID, String loginID, String idCardNumber) { 
		String requestTransactionID = Long.toString(System.currentTimeMillis());
		CreateForgotPasswordRequest createForgotPasswordRequest = createForgotPasswordRequest(requestTransactionID, channelID, loginID, idCardNumber);
		tmnProfileAdminProxy.createForgotPassword(createForgotPasswordRequest);
	}
	
	public ResetPassword verifyResetPassword(Integer channelID, String resetPasswordToken) {
		String requestTransactionID = Long.toString(System.currentTimeMillis());
		VerifyForgotPasswordRequest request = createVerifyForgotPasswordRequest(requestTransactionID, channelID, resetPasswordToken);
		VerifyForgotPasswordResponse response = tmnProfileAdminProxy.verifyForgotPassword(request);
		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setLoginID(response.getLoginId());
		resetPassword.setTruemoneyID(response.getTmnId());
		resetPassword.setMobileNumber(response.getMobile());
		return resetPassword;
	}

	public void confirmResetPassword(Integer channelID, String newPassword, String loginID, String resetPasswordToken) {
		String requestTransactionID = Long.toString(System.currentTimeMillis());
		ConfirmForgotPasswordRequest request = createConfirmForgotPasswordRequest(requestTransactionID, channelID, newPassword, loginID, resetPasswordToken);
		tmnProfileAdminProxy.confirmForgotPassword(request);
	}

    private ConfirmForgotPasswordRequest createConfirmForgotPasswordRequest(String requestTransactionID, Integer channelID, String newPassword, String loginID, String resetPasswordToken) {
    	ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
    	confirmForgotPasswordRequest.setRequestTransactionId(requestTransactionID);
    	confirmForgotPasswordRequest.setAdminSecurityContext(createSecurityContext(requestTransactionID));
    	confirmForgotPasswordRequest.setForgotToken(resetPasswordToken);
    	confirmForgotPasswordRequest.setChannelId(channelID);
    	confirmForgotPasswordRequest.setNewPin(newPassword);
    	confirmForgotPasswordRequest.setLoginId(loginID);
        return confirmForgotPasswordRequest;
	}

	private VerifyForgotPasswordRequest createVerifyForgotPasswordRequest(String requestTransactionID, Integer channelID, String resetPasswordToken) {
    	VerifyForgotPasswordRequest verifyForgotPasswordRequest = new VerifyForgotPasswordRequest();
    	verifyForgotPasswordRequest.setRequestTransactionId(requestTransactionID);
    	verifyForgotPasswordRequest.setAdminSecurityContext(createSecurityContext(requestTransactionID));
    	verifyForgotPasswordRequest.setForgotToken(resetPasswordToken);
    	verifyForgotPasswordRequest.setChannelId(channelID);
        return verifyForgotPasswordRequest;
    }
	
	private CreateForgotPasswordRequest createForgotPasswordRequest(String requestTransactionID, Integer channelID, String loginID, String idCardNumber) {
		CreateForgotPasswordRequest createForgotPasswordRequest = new CreateForgotPasswordRequest();
		createForgotPasswordRequest.setRequestTransactionId(requestTransactionID);
		createForgotPasswordRequest.setAdminSecurityContext(createSecurityContext(requestTransactionID));
		createForgotPasswordRequest.setChannelId(channelID);
		createForgotPasswordRequest.setThaiId(idCardNumber);
		createForgotPasswordRequest.setLoginId(loginID);
        return createForgotPasswordRequest;
	}
	
	private String encryptSHA1(String requestTransactionID) {
		String initiator = tmnProfileInitiator != null ? tmnProfileInitiator.toLowerCase() : "";
		String tempEncrypted = HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase();
		return HashPasswordUtil.encryptSHA1(requestTransactionID + tempEncrypted).toUpperCase();
	}
	
	private AdminSecurityContext createSecurityContext(String requestTransactionID) {
		String encryptedPin = encryptSHA1(requestTransactionID);
        return new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
	}

	public void setTmnProfileAdminProxy(TmnProfileAdminProxy tmnProfileAdminProxy) {
		this.tmnProfileAdminProxy = tmnProfileAdminProxy;
	}

}
