package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.codehaus.jackson.map.annotate.JsonDeserialize;


public class TopUpQuote implements Serializable {


	private static final long serialVersionUID = 2372537225330036311L;

	private String ID;
	@JsonDeserialize(as=DirectDebit.class)
	private SourceOfFund sourceOfFund;
	private String accessTokenID;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;
	
	public TopUpQuote() {
		super();
	}
	
	public TopUpQuote(String ID, SourceOfFund sourceOfFund,
			String accessTokenID, String username, BigDecimal amount,
			BigDecimal topUpFee) {
		super();
		this.ID = ID;
		this.sourceOfFund = sourceOfFund;
		this.accessTokenID = accessTokenID;
		this.username = username;
		this.amount = amount;
		this.topUpFee = topUpFee;
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

	@Override
	public String toString() {
		return "TopUpQuote [ID=" + ID + ", sourceOfFund=" + sourceOfFund
				+ ", accessTokenID=" + accessTokenID + ", username=" + username
				+ ", amount=" + amount + ", topUpFee=" + topUpFee + "]";
	}
	
	
}
