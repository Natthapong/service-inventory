package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPassword implements Serializable {

	private static final long serialVersionUID = -4443827326120382776L;
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
