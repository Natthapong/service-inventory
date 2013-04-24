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

	private String mobileNumber;
	private String referenceCode;
	private String otpString;

	public OTP() {
	}

	public OTP(String mobileNumber, String referenceCode) {
		this.mobileNumber = mobileNumber;
		this.referenceCode = referenceCode;
	}

	public OTP(String mobileNumber, String referenceCode, String otpString) {
		this.mobileNumber = mobileNumber;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("ref code: ", this.referenceCode)
			.append("otpString: ", this.otpString)
			.append("mobileNumber: ", this.mobileNumber)
			.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OTP other = (OTP) obj;
		if (mobileNumber == null) {
			if (other.mobileNumber != null)
				return false;
		} else if (!mobileNumber.equals(other.mobileNumber))
			return false;
		if (otpString == null) {
			if (other.otpString != null)
				return false;
		} else if (!otpString.equals(other.otpString))
			return false;
		if (referenceCode == null) {
			if (other.referenceCode != null)
				return false;
		} else if (!referenceCode.equals(other.referenceCode))
			return false;
		return true;
	}
	
}
