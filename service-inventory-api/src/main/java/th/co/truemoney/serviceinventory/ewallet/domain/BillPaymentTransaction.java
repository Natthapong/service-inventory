package th.co.truemoney.serviceinventory.ewallet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPaymentTransaction extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;
	
	private FailStatus failStatus;
	
	private BillPaymentTransactionConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		PCS_FAILED, UMARKET_FAILED, TPP_FAILED, UNKNOWN_FAILED;
	}
	
	public BillPaymentTransaction() {

	}
	
	public BillPaymentTransaction(BillPaymentDraftTransaction billPaymentDraftTransaction) {
		if (billPaymentDraftTransaction == null || billPaymentDraftTransaction.getStatus() != DraftTransaction.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}
		this.ID = billPaymentDraftTransaction.getID();
		this.draftTransaction = billPaymentDraftTransaction;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public BillPaymentDraftTransaction getDraftTransaction() {
		return (BillPaymentDraftTransaction) super.getDraftTransaction();
	}

	public void setDraftTransaction(BillPaymentDraftTransaction billPaymentDraftTransaction) {
		super.setDraftTransaction(billPaymentDraftTransaction);
	}
	
	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
	}

	public BillPaymentTransactionConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(BillPaymentTransactionConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	@Override
	public String toString() {
		return "BillPaymentTransaction [failStatus=" + failStatus
				+ ", confirmationInfo=" + confirmationInfo + ", ID=" + ID
				+ ", type=" + type + ", draftTransaction=" + draftTransaction
				+ ", status=" + status + "]";
	}
	
}
