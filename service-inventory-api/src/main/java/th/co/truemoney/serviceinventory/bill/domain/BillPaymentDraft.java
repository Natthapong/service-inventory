package th.co.truemoney.serviceinventory.bill.domain;

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
	
	private String selectedSourceOfFundType;
	
	private String otpReferenceCode;

	public BillPaymentDraft() {
		this(null, null, null);
	}

	public BillPaymentDraft(String ID) {
		this(ID, null, null);
	}

	public BillPaymentDraft(String ID, Status status) {
		this(ID, status, null);
	}

	public BillPaymentDraft(String ID, Status status, Bill billInfo) {
		this.ID = ID;
		this.status = status;
		this.type = DRAFT_TYPE;
		this.billInfo = billInfo;
	}


	public Bill getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(Bill billInfo) {
		this.billInfo = billInfo;
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
	public BillPaySourceOfFund getSelectedSourceOfFund() {
		
		if (selectedSourceOfFundType != null && billInfo != null && billInfo.getSourceOfFundFees() != null) {
			for (BillPaySourceOfFund sourceOfFundFee :  billInfo.getSourceOfFundFees()) {
				if (sourceOfFundFee.getSourceType().equals(selectedSourceOfFundType)) {
					return sourceOfFundFee;
				}
			}
		}
		
		return null;
	}
}
