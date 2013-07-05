package th.co.truemoney.serviceinventory.ewallet.domain;

public class ForgotPassword {
	private String loginID;
	private String idCardNumber;
	
	public ForgotPassword(){
		super();
	}

	public ForgotPassword(String loginID, String idCardNumber) {
		super();
		this.loginID = loginID;
		this.idCardNumber = idCardNumber;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}
	
	

}
