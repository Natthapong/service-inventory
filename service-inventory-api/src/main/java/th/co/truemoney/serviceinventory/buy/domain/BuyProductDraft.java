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
	
	private BuyProduct buyProductInfo;	
	
	private String transactionID;
	
	private String recipientMobileNumber;
	
	private String selectedSourceOfFundType;

	public BuyProductDraft() {
		this(null, null, null, null, null, null, null);
		type = DRAFT_TYPE;
	}

	public BuyProductDraft(String ID) {
		this(ID, null, null, null, null, null, null);
	}

	public BuyProductDraft(String ID, BuyProduct buyProductInfo) {
		this(ID, buyProductInfo, null, null, null, null, null);
	}

	public BuyProductDraft(String ID, BuyProduct buyProductInfo, BigDecimal amount) {
		this(ID, buyProductInfo, amount, null, null, null, null);
	}

	public BuyProductDraft(String ID, BuyProduct buyProductInfo, BigDecimal amount, String transactionID) {
		this(ID, buyProductInfo, amount, transactionID, null, null, null);
	}
	
	public BuyProductDraft(String ID, BuyProduct buyProductInfo, BigDecimal amount, String transactionID, String target) {
		this(ID, buyProductInfo, amount, transactionID, target, null, null);
	}
	
	public BuyProductDraft(String ID, BuyProduct buyProductInfo, BigDecimal amount, String transactionID, String target, String recipientMobileNumber) {
		this(ID, buyProductInfo, amount, transactionID, target, recipientMobileNumber, null);
	}
	
	public BuyProductDraft(String ID, BuyProduct buyProductInfo, BigDecimal amount, String transactionID, String target, String recipientMobileNumber, Status status) {
		if (buyProductInfo == null) {
			buyProductInfo = new BuyProduct();
		}
		this.ID = ID;
		this.status = status;
		this.transactionID = transactionID;
		this.recipientMobileNumber = recipientMobileNumber;
		this.type = DRAFT_TYPE;
		this.buyProductInfo = buyProductInfo;
		this.buyProductInfo.setTarget(target);
		this.buyProductInfo.setAmount(amount);
	}

	public BuyProduct getBuyProductInfo() {
		return buyProductInfo;
	}

	public void setBuyProductInfo(BuyProduct buyProductInfo) {
		this.buyProductInfo = buyProductInfo;
	}

	public String getSelectedSourceOfFundType() {
		return selectedSourceOfFundType;
	}

	public void setSelectedSourceOfFundType(String selectedSourceOfFundType) {
		this.selectedSourceOfFundType = selectedSourceOfFundType;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getRecipientMobileNumber() {
		return recipientMobileNumber;
	}

	public void setRecipientMobileNumber(String recipientMobileNumber) {
		this.recipientMobileNumber = recipientMobileNumber;
	}
	
}
