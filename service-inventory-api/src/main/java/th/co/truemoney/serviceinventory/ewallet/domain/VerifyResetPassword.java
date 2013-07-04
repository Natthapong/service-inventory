package th.co.truemoney.serviceinventory.ewallet.domain;

public class VerifyResetPassword {

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
