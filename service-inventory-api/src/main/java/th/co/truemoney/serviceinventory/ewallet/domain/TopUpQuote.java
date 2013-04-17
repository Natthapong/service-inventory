package th.co.truemoney.serviceinventory.ewallet.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpQuote extends DraftTransaction {

	private static final String DRAFT_TYPE = "topUpQuote";

	private static final long serialVersionUID = 2372537225330036311L;

	private SourceOfFund sourceOfFund;

	private BigDecimal amount;

	private BigDecimal topUpFee;

	private String otpReferenceCode;

	public TopUpQuote() {
		type = DRAFT_TYPE;
	}

	public TopUpQuote(BigDecimal amount) {
		this.amount = amount;
		type = DRAFT_TYPE;
	}

	public TopUpQuote(String ID, SourceOfFund sourceOfFund,
			String accessTokenID, BigDecimal amount,
			BigDecimal topUpFee) {
		this.ID = ID;
		this.sourceOfFund = sourceOfFund;
		this.accessTokenID = accessTokenID;
		this.amount = amount;
		this.topUpFee = topUpFee;
		this.status = TopUpQuote.Status.CREATED;
		type = DRAFT_TYPE;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String accessTokenID) {
		this.accessTokenID = accessTokenID;
	}

	public SourceOfFund getSourceOfFund() {
		return sourceOfFund;
	}

	public void setSourceOfFund(SourceOfFund sourceOfFund) {
		this.sourceOfFund = sourceOfFund;
	}

	public BigDecimal getTopUpFee() {
		return topUpFee;
	}

	public void setTopUpFee(BigDecimal topUpFee) {
		this.topUpFee = topUpFee;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}

	@Override
	public String toString() {
		return "TopUpQuote [ID=" + ID + ", sourceOfFund=" + sourceOfFund
				+ ", accessTokenID=" + accessTokenID
				+ ", amount=" + amount + ", topUpFee=" + topUpFee
				+ ", status=" + status + "]";
	}


}
