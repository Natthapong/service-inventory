package th.co.truemoney.serviceinventory.transfer.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class P2PDraftTransaction extends DraftTransaction {

	private static final String DRAFT_TYPE = "p2pDraftTransaction";

	private static final long serialVersionUID = -5651007822743041981L;

	private String mobileNumber;

	private BigDecimal amount;

	private String fullname;

	private String otpReferenceCode;

	public P2PDraftTransaction() {
		type = DRAFT_TYPE;
	}

	public P2PDraftTransaction(String mobileNumber, BigDecimal amount) {
		this.mobileNumber = mobileNumber;
		this.amount = amount;
		type = DRAFT_TYPE;
	}

	public P2PDraftTransaction(String mobileNumber, BigDecimal amount,
			String ID, String accessTokenID, String fullname,
			String otpReferenceCode) {
		this.mobileNumber = mobileNumber;
		this.amount = amount;
		this.ID = ID;
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

	@Override
	public String toString() {
		return "P2PDraftTransaction [mobileNumber=" + mobileNumber
				+ ", amount=" + amount + ", fullname=" + fullname
				+ ", otpReferenceCode=" + otpReferenceCode + ", ID=" + ID
				+ ", accessTokenID=" + accessTokenID + ", type=" + type
				+ ", status=" + status + "]";
	}

}
