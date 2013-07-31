package th.co.truemoney.serviceinventory.transfer.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class P2PTransferTransaction extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;

	private FailStatus failStatus;
	
	private ServiceInventoryException failCause;
	
	private P2PTransactionConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		UMARKET_FAILED, UNKNOWN_FAILED;
	}

	public P2PTransferTransaction() {

	}

	public P2PTransferTransaction(P2PTransferDraft p2pTransferDraft) {
		if (p2pTransferDraft == null || p2pTransferDraft.getStatus() != P2PTransferDraft.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}
		this.ID = p2pTransferDraft.getID();
		this.draftTransaction = p2pTransferDraft;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public P2PTransferDraft getDraftTransaction() {
		return (P2PTransferDraft) super.getDraftTransaction();
	}

	public void setDraftTransaction(P2PTransferDraft p2pTransferDraft) {
		super.setDraftTransaction(p2pTransferDraft);
	}

	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
	}

	public P2PTransactionConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(P2PTransactionConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}
	
	public ServiceInventoryException getFailCause() {
		return failCause;
	}

	public void setFailCause(ServiceInventoryException failCause) {
		this.failCause = failCause;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.appendSuper(super.toString())
				.append("failStatus: ", this.getFailStatus())
				.append("confirmationInfo: ", this.getConfirmationInfo())
				.toString();
	}

}
