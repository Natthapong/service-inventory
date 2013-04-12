package th.co.truemoney.serviceinventory.bill.domain;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Bill extends DraftTransaction {

	private static final long serialVersionUID = 2049045334050859727L;

	private static final String DRAFT_TYPE = "bill";

	private BillInfo billInfo;
	
	private String selectedSourceOfFundType;
	
	private String otpReferenceCode;

	public Bill() {
		this(null, null, null);
	}

	public Bill(String ID) {
		this(ID, null, null);
	}

	public Bill(String ID, Status status) {
		this(ID, status, null);
	}

	public Bill(String ID, Status status, BillInfo billInfo) {
		this.ID = ID;
		this.status = status;
		this.type = DRAFT_TYPE;
		this.billInfo = billInfo;
	}


	public BillInfo getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(BillInfo billInfo) {
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
	public SourceOfFundFee getSelectedSourceOfFund() {
		
		if (selectedSourceOfFundType != null && billInfo != null && billInfo.getSourceOfFundFees() != null) {
			for (SourceOfFundFee sourceOfFundFee :  billInfo.getSourceOfFundFees()) {
				if (sourceOfFundFee.getSourceType().equals(selectedSourceOfFundType)) {
					return sourceOfFundFee;
				}
			}
		}
		
		return null;
	}
}
