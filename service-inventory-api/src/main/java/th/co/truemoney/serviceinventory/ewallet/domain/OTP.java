package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class OTP implements Serializable {
	
	private static final long serialVersionUID = -4332227740146738592L;
	private String mobileno;
	private String otpString;
	private String otpReferenceCode;
	private String checksum;
		
	public OTP() {
		super();
	}
	public OTP(String mobileno, String otpString, String otpReferenceCode) {
		this.mobileno = mobileno;
		this.otpString = otpString;
		this.otpReferenceCode = otpReferenceCode;
	}	
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
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
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("mobileno: ", this.mobileno)
			.append("otpString: ", this.otpString)
			.append("otpReferenceCode: ", this.otpReferenceCode)
			.append("checksum: ", this.checksum)
			.toString();
	}
	
}
