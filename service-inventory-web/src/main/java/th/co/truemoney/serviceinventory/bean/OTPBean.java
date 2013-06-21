package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class OTPBean implements Serializable {
	
	private static final long serialVersionUID = -4332227740146738592L;
	private String mobileNumber;
	private String otpString;
	private String otpReferenceCode;
		
	public OTPBean() {
		super();
	}
	public OTPBean(String mobileNumber, String otpString, String otpReferenceCode) {
		this.mobileNumber = mobileNumber;
		this.otpString = otpString;
		this.otpReferenceCode = otpReferenceCode;
	}	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("mobileNumber: ", this.mobileNumber)
			.append("otpString: ", this.otpString)
			.append("otpReferenceCode: ", this.otpReferenceCode)
			.toString();
	}
	
}
