package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;


public class TopUpQuote implements Serializable {


	private static final long serialVersionUID = 2372537225330036311L;

	private String ID;
	private DirectDebit directDebit;
	private String accessTokenID;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;

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

	public DirectDebit getDirectDebit() {
		return directDebit;
	}
	
	public void setDirectDebit(DirectDebit directDebit) {
		this.directDebit = directDebit;
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
}
