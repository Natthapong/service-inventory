package th.co.truemoney.serviceinventory.ewallet.domain;

public enum TopUpQuoteStatus {

	CREATED("CREATED"), OTP_SENT("SENT"), OTP_CONFIRMED("CONFIRMED");

	private String topUpQuoteStatus;

	private TopUpQuoteStatus(String topUpQuoteStatus) {
		this.topUpQuoteStatus = topUpQuoteStatus;
	}

	public String getStatus() {
		return topUpQuoteStatus;
	}

}
