package th.co.truemoney.serviceinventory.buy.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BuyEPINDraft extends DraftTransaction {

	private static final String DRAFT_TYPE = "buyEPINDraft";
	
	private static final long serialVersionUID = 9001829071748468624L;
	
	private String mobileNumber;

	private BigDecimal amount;
	
	public BuyEPINDraft() {
		type = DRAFT_TYPE;
	}

	public BuyEPINDraft(String mobileNumber, BigDecimal amount) {
		this.mobileNumber = mobileNumber;
		this.amount = amount;
		type = DRAFT_TYPE;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
