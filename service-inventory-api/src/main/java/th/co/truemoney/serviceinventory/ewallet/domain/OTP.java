package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class OTP implements Serializable {
	
	private static final long serialVersionUID = -4332227740146738592L;
	private String otpString;
	private String checksum;
		
	public OTP() {
		super();
	}
	
	public OTP(String otpString, String checksum) {
		this.otpString = otpString;
		this.checksum = checksum;
	}

	public String getOtpString() {
		return otpString;
	}

	public void setOtpString(String otpString) {
		this.otpString = otpString;
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
			.append("otpString: ", this.otpString)
			.append("checksum: ", this.checksum)
			.toString();
	}
	
}
