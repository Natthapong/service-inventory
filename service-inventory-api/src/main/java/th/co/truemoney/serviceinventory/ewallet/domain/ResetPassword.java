package th.co.truemoney.serviceinventory.ewallet.domain;

public class ResetPassword {
	
	private String token;
	private String newPassword;
	private String loginID;
	private String truemoneyID;
	private String mobileNumber;
	
	public ResetPassword() {
		super();
	}	
	public ResetPassword(String token, String newPassword) {
		super();
		this.token = token;
		this.newPassword = newPassword;
	}	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getTruemoneyID() {
		return truemoneyID;
	}
	public void setTruemoneyID(String truemoneyID) {
		this.truemoneyID = truemoneyID;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
}
