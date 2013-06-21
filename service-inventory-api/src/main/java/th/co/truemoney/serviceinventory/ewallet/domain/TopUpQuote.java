package th.co.truemoney.serviceinventory.ewallet.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.appendSuper(super.toString())
				.append("sourceOfFund: ", this.getSourceOfFund())
				.append("amount: ", this.getAmount())
				.append("topUpFee: ", this.getTopUpFee())
				.toString();
	}

}
