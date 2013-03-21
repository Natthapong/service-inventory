package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class TopUpOrder implements Serializable {

	private static final long serialVersionUID = 2325219087645032462L;

	private String ID;	
	@JsonDeserialize(as=DirectDebit.class)
	private SourceOfFund sourceOfFund;
	private String accessTokenID;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;
	private String otpReferenceCode;
	private TopUpStatus status = TopUpStatus.AWAITING_CONFIRM;
	private TopUpConfirmationInfo confirmationInfo;

	public TopUpOrder() {
		super();
	}

	public TopUpOrder(TopUpQuote quote) {
		this.ID = quote.getID();
		this.sourceOfFund = quote.getSourceOfFund();
		this.accessTokenID = quote.getAccessTokenID();
		this.username = quote.getUsername();
		this.amount = quote.getAmount();
		this.topUpFee = quote.getTopUpFee();

		this.status = TopUpStatus.AWAITING_CONFIRM;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public SourceOfFund getSourceOfFund() {
		return sourceOfFund;
	}

	public void setSourceOfFund(SourceOfFund sourceOfFund) {
		this.sourceOfFund = sourceOfFund;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String accessTokenID) {
		this.accessTokenID = accessTokenID;
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

	public TopUpStatus getStatus() {
		return status;
	}

	public void setStatus(TopUpStatus status) {
		this.status = status;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}

	public TopUpConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(TopUpConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	@Override
	public String toString() {
		return new StringBuffer()
		.append(this.ID)
		.append(this.accessTokenID)
		.append(this.username)
		.append(this.amount)
		.append(this.topUpFee)
		.append(this.status)
		.toString();
	}
		
}
