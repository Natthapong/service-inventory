package th.co.truemoney.serviceinventory.topup.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpMobileDraft extends DraftTransaction {

	private static final long serialVersionUID = 1L;
	
	private static final String DRAFT_TYPE = "mobileTopUp";

	TopUpMobile topUpMobileInfo;
	
	private BigDecimal amount = BigDecimal.ZERO;
	
	private String selectedSourceOfFundType;
	private String otpReferenceCode;

	private String transactionID;

	public TopUpMobileDraft() {
		this(null, null, null, null);
	}

	public TopUpMobileDraft(String ID) {
		this(ID, null, null, null);
	}

	public TopUpMobileDraft(String ID, TopUpMobile topUpMobileInfo) {
		this(ID, topUpMobileInfo, null, null);
	}

	public TopUpMobileDraft(String ID, TopUpMobile topUpMobileInfo, BigDecimal amount) {
		this(ID, topUpMobileInfo, amount, null);
	}

	public TopUpMobileDraft(String ID, TopUpMobile topUpMobileInfo, BigDecimal amount, String transactionID) {
		this(ID, topUpMobileInfo, amount, transactionID, null);
	}

	public TopUpMobileDraft(String ID, TopUpMobile topUpMobileInfo, BigDecimal amount, String transactionID, Status status) {
		this.ID = ID;
		this.status = status;
		this.amount = amount;
		this.transactionID = transactionID;
		this.type = DRAFT_TYPE;
		this.topUpMobileInfo = topUpMobileInfo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSelectedSourceOfFundType() {
		return selectedSourceOfFundType;
	}

	public void setSelectedSourceOfFundType(String selectedSourceOfFundType) {
		this.selectedSourceOfFundType = selectedSourceOfFundType;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}	
	
	@JsonIgnore
	public BillPaySourceOfFund getSelectedSourceOfFund() {

		if (selectedSourceOfFundType != null && topUpMobileInfo != null && topUpMobileInfo.getSourceOfFundFees() != null) {
			for (BillPaySourceOfFund sourceOfFundFee :  topUpMobileInfo.getSourceOfFundFees()) {
				if (sourceOfFundFee.getSourceType().equals(selectedSourceOfFundType)) {
					return sourceOfFundFee;
				}
			}
		}

		return null;
	}

}
