package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OTP implements Serializable {

	private static final long serialVersionUID = -4332227740146738592L;

	private String mobileNo;
	private String referenceCode;
	private String otpString;

	public OTP() {
	}

	public OTP(String mobileNo, String referenceCode) {
		this.mobileNo = mobileNo;
		this.referenceCode = referenceCode;
	}

	public OTP(String mobileNo, String referenceCode, String otpString) {
		this.mobileNo = mobileNo;
		this.referenceCode = referenceCode;
		this.otpString = otpString;
	}

	public String getOtpString() {
		return otpString;
	}

	public void setOtpString(String otpString) {
		this.otpString = otpString;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("ref code: ", this.referenceCode)
			.append("otpString: ", this.otpString)
			.append("mobile no: ", this.mobileNo)
			.toString();
	}

}
