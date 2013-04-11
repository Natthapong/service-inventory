package th.co.truemoney.serviceinventory.transfer.domain;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class P2PTransaction extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;

	private FailStatus failStatus;

	private P2PTransactionConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		UMARKET_FAILED, UNKNOWN_FAILED;
	}

	public P2PTransaction() {

	}

	public P2PTransaction(P2PDraftTransaction p2pDraftTransaction) {
		if (p2pDraftTransaction == null || p2pDraftTransaction.getStatus() != DraftTransaction.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}
		this.ID = p2pDraftTransaction.getID();
		this.draftTransaction = p2pDraftTransaction;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public P2PDraftTransaction getDraftTransaction() {
		return (P2PDraftTransaction) super.getDraftTransaction();
	}

	public void setDraftTransaction(P2PDraftTransaction p2pDraftTransaction) {
		super.setDraftTransaction(p2pDraftTransaction);
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

	@Override
	public String toString() {
		return "P2PTransaction [failStatus=" + failStatus
				+ ", confirmationInfo=" + confirmationInfo + ", ID=" + ID
				+ ", type=" + type + ", draftTransaction=" + draftTransaction
				+ ", status=" + status + "]";
	}

}
