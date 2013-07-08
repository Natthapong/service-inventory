package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ForgotPasswordHandler;

public class ForgotPasswordBuilder {
	
	private ForgotPasswordHandler forgotPasswordFacade;
	private Integer channelID;
	private String resetPasswordToken;
	private String newPassword;
	private String loginID;
	private String idCardNumber;

	@Autowired(required = false)
    public ForgotPasswordBuilder(ForgotPasswordHandler forgotPasswordFacade) {
		this.forgotPasswordFacade = forgotPasswordFacade;
	}

	public ForgotPasswordBuilder withToken(String resetPasswordToken) {
    	this.resetPasswordToken = resetPasswordToken;
    	return this;
    }
    
	public ForgotPasswordBuilder fromChannel(Integer channelID) {
    	this.channelID = channelID;
    	return this;
	}

	public ForgotPasswordBuilder withNewPassword(String loginID, String newPassword) {
		this.loginID = loginID;
    	this.newPassword = newPassword;
    	return this;
	}

    public ResetPassword verifyResetPassword() {
        Validate.notNull(resetPasswordToken, "data missing. reset password token ?");
        Validate.notNull(channelID, "data missing. from which channel ?");
        
        return forgotPasswordFacade.verifyResetPassword(channelID, resetPasswordToken);
    }
    
    public void confirmResetPassword() {
    	Validate.notNull(loginID, "data missing. login ID?");
        Validate.notNull(newPassword, "data missing. new password ?");
        Validate.notNull(channelID, "data missing. from which channel ?");
        Validate.notNull(resetPasswordToken, "data missing. reset password token ?");
        
        forgotPasswordFacade.confirmResetPassword(channelID, newPassword, loginID, resetPasswordToken);
    }

	public ForgotPasswordBuilder withLogin(String loginID) {
		this.loginID = loginID;
		return this;
	}

	public ForgotPasswordBuilder withIdCardNumber(String idcard) {
		this.idCardNumber = idcard;
		return this;
	}

	public void createForgotPassword() {
		Validate.notNull(loginID, "data missing. login ID?");
		Validate.notNull(idCardNumber, "data missing. id card number?");
		Validate.notNull(channelID, "data missing. from which channel ?");
		
		forgotPasswordFacade.createForgotpassword(channelID, loginID, idCardNumber);
	}
}
