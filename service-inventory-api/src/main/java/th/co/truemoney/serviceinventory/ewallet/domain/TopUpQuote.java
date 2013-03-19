package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;


public class TopUpQuote implements Serializable {


	private static final long serialVersionUID = 2372537225330036311L;

	private String id;
	private SourceOfFund sourceOfFund;
	private String accessToken;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SourceOfFund getSourceOfFund() {
		return sourceOfFund;
	}
	
	public void setSourceOfFund(SourceOfFund sourceOfFund) {
		this.sourceOfFund = sourceOfFund;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
