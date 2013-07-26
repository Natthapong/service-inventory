package th.co.truemoney.serviceinventory.bill.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPaymentTransaction extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;

	private FailStatus failStatus;
	
	private ServiceInventoryException failCause;

	private BillPaymentConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		PCS_FAILED, UMARKET_FAILED, TPP_FAILED, UNKNOWN_FAILED;
	}

	public BillPaymentTransaction() {

	}

	public BillPaymentTransaction(BillPaymentDraft draft) {
		if (draft == null || draft.getStatus() != DraftTransaction.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad draft data");
		}
		this.ID = draft.getID(); // Transaction ID == Draft ID
		this.draftTransaction = draft;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public BillPaymentDraft getDraftTransaction() {
		return (BillPaymentDraft) super.getDraftTransaction();
	}

	public void setDraftTransaction(BillPaymentDraft bill) {
		super.setDraftTransaction(bill);
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
        	.append("failStatus", this.getFailStatus())
        	.append("confirmationInfo", this.getConfirmationInfo())
        	.toString();
    }

}
