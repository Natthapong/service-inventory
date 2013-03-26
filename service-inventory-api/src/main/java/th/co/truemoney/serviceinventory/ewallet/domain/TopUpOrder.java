package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpOrder implements Serializable {

	private static final long serialVersionUID = 2325219087645032462L;

	private String ID;
	private SourceOfFund sourceOfFund;
	private String accessTokenID;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;
	private TopUpOrderStatus status = TopUpOrderStatus.ORDER_VERIFIED;
	private TopUpConfirmationInfo confirmationInfo;

	public TopUpOrder() {
		super();
	}


	public TopUpOrder(String iD, SourceOfFund sourceOfFund,
			String accessTokenID, String username, BigDecimal amount,
			BigDecimal topUpFee, TopUpOrderStatus status,
			TopUpConfirmationInfo confirmationInfo) {
		super();
		ID = iD;
		this.sourceOfFund = sourceOfFund;
		this.accessTokenID = accessTokenID;
		this.username = username;
		this.amount = amount;
		this.topUpFee = topUpFee;
		this.status = status;
		this.confirmationInfo = confirmationInfo;
	}


	public TopUpOrder(TopUpQuote quote) {
		this.ID = quote.getID();
		this.sourceOfFund = quote.getSourceOfFund();
		this.accessTokenID = quote.getAccessTokenID();
		this.username = quote.getUsername();
		this.amount = quote.getAmount();
		this.topUpFee = quote.getTopUpFee();

		this.status = TopUpOrderStatus.ORDER_VERIFIED;
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

	public TopUpOrderStatus getStatus() {
		return status;
	}

	public void setStatus(TopUpOrderStatus status) {
		this.status = status;
	}

	public TopUpConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(TopUpConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	@Override
	public String toString() {
		return "TopUpOrder [ID=" + ID + ", sourceOfFund=" + sourceOfFund
				+ ", accessTokenID=" + accessTokenID + ", username=" + username
				+ ", amount=" + amount + ", topUpFee=" + topUpFee
				+ ", status=" + status + ", confirmationInfo=" + confirmationInfo + "]";
	}

}
