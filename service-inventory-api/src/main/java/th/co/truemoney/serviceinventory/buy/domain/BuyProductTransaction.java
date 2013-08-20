package th.co.truemoney.serviceinventory.buy.domain;

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
public class BuyProductTransaction extends Transaction {

	private static final long serialVersionUID = -3546197537668299129L;

	private FailStatus failStatus;
	
	private ServiceInventoryException failCause;
	
	private BuyProductConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		UMARKET_FAILED, UNKNOWN_FAILED;
	}

	public BuyProductTransaction() {
		super();
	}

	public BuyProductTransaction(BuyProductDraft buyEPINDraft) {
		if (buyEPINDraft == null || buyEPINDraft.getStatus() != BuyProductDraft.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad draft data");
		}
		this.ID = buyEPINDraft.getID();
		this.draftTransaction = buyEPINDraft;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public BuyProductDraft getDraftTransaction() {
		return (BuyProductDraft) super.getDraftTransaction();
	}

	public void setDraftTransaction(BuyProductDraft buyEPINDraft) {
		super.setDraftTransaction(buyEPINDraft);
	}

	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
	}

	public BuyProductConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(BuyProductConfirmationInfo confirmationInfo) {
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
