package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpQuote implements Serializable {

	private static final long serialVersionUID = 2372537225330036311L;

	private String ID;

	private SourceOfFund sourceOfFund;

	private String accessTokenID;

	private String username;

	private BigDecimal amount;

	private BigDecimal topUpFee;

	private String otpReferenceCode;

	private TopUpQuoteStatus status;

	public TopUpQuote() {
	}

	public TopUpQuote(BigDecimal amount) {
		this.amount = amount;
	}

	public TopUpQuote(String ID, SourceOfFund sourceOfFund,
			String accessTokenID, String username, BigDecimal amount,
			BigDecimal topUpFee) {
		this.ID = ID;
		this.sourceOfFund = sourceOfFund;
		this.accessTokenID = accessTokenID;
		this.username = username;
		this.amount = amount;
		this.topUpFee = topUpFee;
		this.status = TopUpQuoteStatus.CREATED;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public TopUpQuoteStatus getStatus() {
		return status;
	}

	public void setStatus(TopUpQuoteStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TopUpQuote [ID=" + ID + ", sourceOfFund=" + sourceOfFund
				+ ", accessTokenID=" + accessTokenID + ", username=" + username
				+ ", amount=" + amount + ", topUpFee=" + topUpFee
				+ ", status=" + status + "]";
	}


}
