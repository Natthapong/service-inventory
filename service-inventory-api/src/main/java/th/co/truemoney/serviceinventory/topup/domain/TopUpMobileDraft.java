package th.co.truemoney.serviceinventory.topup.domain;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
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

	public TopUpMobile getTopUpMobileInfo() {
		return topUpMobileInfo;
	}

	public void setTopUpMobileInfo(TopUpMobile topUpMobileInfo) {
		this.topUpMobileInfo = topUpMobileInfo;
	}

	private String selectedSourceOfFundType;
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
		if (topUpMobileInfo == null) {
			topUpMobileInfo = new TopUpMobile();
		}
		this.ID = ID;
		this.status = status;
		this.transactionID = transactionID;
		this.type = DRAFT_TYPE;
		this.topUpMobileInfo = topUpMobileInfo;
		this.topUpMobileInfo.setAmount(amount);
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

	@JsonIgnore
	public SourceOfFund getSelectedSourceOfFund() {

		if (selectedSourceOfFundType != null && topUpMobileInfo != null && topUpMobileInfo.getSourceOfFundFees() != null) {
			for (SourceOfFund sourceOfFundFee :  topUpMobileInfo.getSourceOfFundFees()) {
				if (sourceOfFundFee.getSourceType().equals(selectedSourceOfFundType)) {
					return sourceOfFundFee;
				}
			}
		}

		return null;
	}

}
