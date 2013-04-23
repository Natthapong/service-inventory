package th.co.truemoney.serviceinventory.bill.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPaymentDraft extends DraftTransaction {

	private static final long serialVersionUID = 2049045334050859727L;

	private static final String DRAFT_TYPE = "bill";

	private Bill billInfo;

	private BigDecimal amount;
	private String selectedSourceOfFundType;
	private String otpReferenceCode;

	private String transactionID;

	public BillPaymentDraft() {
		this(null, null, null, null);
	}

	public BillPaymentDraft(String ID) {
		this(ID, null, null, null);
	}

	public BillPaymentDraft(String ID, Bill billInfo) {
		this(ID, billInfo, null, null);
	}

	public BillPaymentDraft(String ID, Bill billInfo, BigDecimal amount) {
		this(ID, billInfo, amount, null);
	}

	public BillPaymentDraft(String ID, Bill billInfo, BigDecimal amount, String transactionID) {
		this(ID, billInfo, amount, transactionID, null);
	}


	public BillPaymentDraft(String ID, Bill billInfo, BigDecimal amount, String transactionID, Status status) {
		this.ID = ID;
		this.status = status;
		this.amount = amount;
		this.transactionID = transactionID;
		this.type = DRAFT_TYPE;
		this.billInfo = billInfo;
	}


	public Bill getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(Bill billInfo) {
		this.billInfo = billInfo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}

	public void setSelectedSourceOfFundType(String selectedSourceOfFundType) {
		this.selectedSourceOfFundType = selectedSourceOfFundType;
	}

	public String getSelectedSourceOfFundType() {
		return selectedSourceOfFundType;
	}

	@JsonIgnore
	public SourceOfFund getSelectedSourceOfFund() {

		if (selectedSourceOfFundType != null && billInfo != null && billInfo.getSourceOfFundFees() != null) {
			for (SourceOfFund sourceOfFundFee :  billInfo.getSourceOfFundFees()) {
				if (sourceOfFundFee.getSourceType().equals(selectedSourceOfFundType)) {
					return sourceOfFundFee;
				}
			}
		}

		return null;
	}
}
