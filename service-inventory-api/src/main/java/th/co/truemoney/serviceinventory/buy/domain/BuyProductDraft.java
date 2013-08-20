package th.co.truemoney.serviceinventory.buy.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BuyProductDraft extends DraftTransaction {

	private static final String DRAFT_TYPE = "buyProductDraft";
	
	private static final long serialVersionUID = 9001829071748468624L;
	
	private String target;
	
	private String recipientMobileNumber;

	private BigDecimal amount;
	
	public BuyProductDraft() {
		type = DRAFT_TYPE;
	}

	public BuyProductDraft(String target, String recipientMobileNumber, BigDecimal amount) {
		this.target = target;
		this.recipientMobileNumber = recipientMobileNumber;
		this.amount = amount;
		type = DRAFT_TYPE;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getRecipientMobileNumber() {
		return recipientMobileNumber;
	}

	public void setRecipientMobileNumber(String recipientMobileNumber) {
		this.recipientMobileNumber = recipientMobileNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
