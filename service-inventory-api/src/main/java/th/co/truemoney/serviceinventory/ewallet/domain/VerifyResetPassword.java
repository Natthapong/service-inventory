package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class VerifyResetPassword implements Serializable {

	private static final long serialVersionUID = 7741432933056303689L;
	private String resetPasswordID;
	private OTP otp;
	
	public String getResetPasswordID() {
		return resetPasswordID;
	}
	public void setResetPasswordID(String resetPasswordID) {
		this.resetPasswordID = resetPasswordID;
	}
	public void setOtp(OTP otp) {
		this.otp = otp;
	}
	public OTP getOtp() {
		return otp;
	}
	
}
