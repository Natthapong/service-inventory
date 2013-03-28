package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class P2PDraftTransaction implements Serializable {

	private static final long serialVersionUID = -5651007822743041981L;
	private String mobileNumber;
	private BigDecimal amount;
	private String ID;
	private String accessTokenID;
	private String fullname;
	private String otpReferenceCode;

	public P2PDraftTransaction() {
	}
	
	public P2PDraftTransaction(String mobileNumber, BigDecimal amount,
			String iD, String accessTokenID, String fullname,
			String otpReferenceCode) {
		this.mobileNumber = mobileNumber;
		this.amount = amount;
		ID = iD;
		this.accessTokenID = accessTokenID;
		this.fullname = fullname;
		this.otpReferenceCode = otpReferenceCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String accessTokenID) {
		this.accessTokenID = accessTokenID;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}

}
