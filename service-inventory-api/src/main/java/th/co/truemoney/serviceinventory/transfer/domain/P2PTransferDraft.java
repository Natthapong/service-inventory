package th.co.truemoney.serviceinventory.transfer.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class P2PTransferDraft extends DraftTransaction {

	private static final String DRAFT_TYPE = "p2pTransferDraft";

	private static final long serialVersionUID = -5651007822743041981L;

	private String mobileNumber;

	private BigDecimal amount;

	private String fullname;
	
	private String message;

	public P2PTransferDraft() {
		type = DRAFT_TYPE;
	}

	public P2PTransferDraft(String mobileNumber, BigDecimal amount) {
		this.mobileNumber = mobileNumber;
		this.amount = amount;
		type = DRAFT_TYPE;
	}

	public P2PTransferDraft(String mobileNumber, BigDecimal amount,
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
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.appendSuper(super.toString())
				.append("mobileNumber: ", this.getMobileNumber())
				.append("amount: ", this.getAmount())
				.toString();
	}
	
}
