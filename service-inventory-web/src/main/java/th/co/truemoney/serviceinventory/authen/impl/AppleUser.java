package th.co.truemoney.serviceinventory.authen.impl;

import java.io.Serializable;

public class AppleUser implements Serializable {
	
	private static final long serialVersionUID = -8029110247283723529L;
	
	private String otpString;
	
	public String getOtpString() {
		return otpString;
	}
	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}
	
}
