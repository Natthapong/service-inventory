package th.co.truemoney.serviceinventory.topup.domain;


import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpMobileTransaction extends Transaction {

	private static final long serialVersionUID = -7149328094127617653L;

	private FailStatus failStatus;

	private TopUpMobileConfirmationInfo confirmationInfo;
	
	public static enum FailStatus {
		PCS_FAILED, UMARKET_FAILED, TPP_FAILED, UNKNOWN_FAILED;
	}

	public TopUpMobileTransaction() {

	}
	
	public TopUpMobileTransaction(TopUpMobileDraft topUpMobileDraft) {
		if (topUpMobileDraft == null || topUpMobileDraft.getStatus() != DraftTransaction.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}
		this.ID = topUpMobileDraft.getID();
		this.draftTransaction = topUpMobileDraft;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public TopUpMobileDraft getDraftTransaction() {
		return (TopUpMobileDraft) super.getDraftTransaction();
	}

	public void setDraftTransaction(TopUpMobileDraft topUpMobileDraft) {
		super.setDraftTransaction(topUpMobileDraft);
	}

	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
	}

	public TopUpMobileConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(TopUpMobileConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}
	
}
