package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class OTPBean implements Serializable {
	
	private static final long serialVersionUID = -4332227740146738592L;
	private String mobileno;
	private String otpString;
	private String otpReferenceCode;
		
	public OTPBean() {
		super();
	}
	public OTPBean(String mobileno, String otpString, String otpReferenceCode) {
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
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("mobileno: ", this.mobileno)
			.append("otpString: ", this.otpString)
			.append("otpReferenceCode: ", this.otpReferenceCode)
			.toString();
	}
	
}
