package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;


public class TopUpOrder implements Serializable {

	private static final long serialVersionUID = 2325219087645032462L;

	private String id;
	private SourceOfFund sourceOfFund;
	private String accessToken;
	private String username;
	private BigDecimal amount;
	private BigDecimal topUpFee;
	private TopUpStatus status = TopUpStatus.ORDER_PLACED;

	public TopUpOrder() {
	}

	public TopUpOrder(TopUpQuote quote) {
		this.id = quote.getId();
		this.sourceOfFund = quote.getSourceOfFund();
		this.accessToken = quote.getAccessToken();
		this.username = quote.getUsername();
		this.amount = quote.getAmount();
		this.topUpFee = quote.getTopUpFee();

		this.status = TopUpStatus.ORDER_PLACED;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SourceOfFund getSourceOfFund() {
		return sourceOfFund;
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

	public TopUpStatus getStatus() {
		return status;
	}

	public void setStatus(TopUpStatus status) {
		this.status = status;
	}

}
