package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class OTP implements Serializable {
	
	private static final long serialVersionUID = -4332227740146738592L;
	private String otpString;
	private String otpReferenceCode;
	private String checksum;
	
	public String getOtpString() {
		return otpString;
	}
	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}
	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}
	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
}
