package th.co.truemoney.serviceinventory.ewallet.domain;

public enum TopUpStatus {
	
	AWAITING_CONFIRM("AWAITING_CONFIRM"), ORDER_VERIFIED("ORDER_VERIFIED"), PROCESSING("PROCESSING"), CONFIRMED("CONFIRMED"), FAILED("FAILED"), BANK_FAILED("BANK_FAILED"), UMARKET_FAILED("UMARKET_FAILED");
	
	private String topUpStatus;

	private TopUpStatus(String topUpStatus) {
		this.topUpStatus = topUpStatus;
	}

	public String getTopUpStatus() {
		return topUpStatus;
	}
	
}
