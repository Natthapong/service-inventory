package th.co.truemoney.serviceinventory.bill.domain;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPayment extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;
	
	private FailStatus failStatus;
	
	private BillPaymentConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		PCS_FAILED, UMARKET_FAILED, TPP_FAILED, UNKNOWN_FAILED;
	}
	
	public BillPayment() {

	}
	
	public BillPayment(Bill billPaymentDraftTransaction) {
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

	public Bill getDraftTransaction() {
		return (Bill) super.getDraftTransaction();
	}

	public void setDraftTransaction(Bill billPaymentDraftTransaction) {
		super.setDraftTransaction(billPaymentDraftTransaction);
	}
	
	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
	}

	public BillPaymentConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(BillPaymentConfirmationInfo confirmationInfo) {
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
