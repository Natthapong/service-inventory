package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpOrder implements Serializable {

	private static final long serialVersionUID = 2325219087645032462L;

	private String ID;
	private TopUpQuote quote;
	private TopUpOrderStatus status = TopUpOrderStatus.ORDER_VERIFIED;
	private TopUpConfirmationInfo confirmationInfo;

	public TopUpOrder() {
	}

	public TopUpOrder(TopUpQuote quote) {
		if (quote == null || quote.getStatus() != TopUpQuoteStatus.OTP_CONFIRMED) {
			throw new IllegalArgumentException("passing in bad quote data");
		}

		this.ID = quote.getID();
		this.quote = quote;
		this.status = TopUpOrderStatus.ORDER_VERIFIED;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public TopUpQuote getQuote() {
		return quote;
	}

	public void setQuote(TopUpQuote quote) {
		this.quote = quote;
	}

	public TopUpOrderStatus getStatus() {
		return status;
	}

	public void setStatus(TopUpOrderStatus status) {
		this.status = status;
	}

	public TopUpConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(TopUpConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}

	@Override
	public String toString() {
		return "TopUpOrder [ID=" + ID + ", qoute=" + quote
				+ ", status=" + status + ", confirmationInfo=" + confirmationInfo + "]";
	}

}
