package th.co.truemoney.serviceinventory.ewallet.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpOrder extends Transaction {

	private static final long serialVersionUID = 2325219087645032462L;

	private FailStatus failStatus;
	private TopUpConfirmationInfo confirmationInfo;

	public static enum FailStatus {
		BANK_FAILED, UMARKET_FAILED, UNKNOWN_FAILED;
	}

	public TopUpOrder() {
	}

	public TopUpOrder(TopUpQuote quote) {
		if (quote == null || quote.getStatus() != TopUpQuote.Status.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}

		this.ID = quote.getID();
		this.draftTransaction = quote;
		this.status = Transaction.Status.VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public TopUpQuote getQuote() {
		return (TopUpQuote) getDraftTransaction();
	}

	public void setQuote(TopUpQuote quote) {
		setDraftTransaction(quote);
	}

	public TopUpConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(TopUpConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	public FailStatus getFailStatus() {
		return failStatus;
	}

	public void setFailStatus(FailStatus failStatus) {
		this.status = Transaction.Status.FAILED;
		this.failStatus = failStatus;
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
