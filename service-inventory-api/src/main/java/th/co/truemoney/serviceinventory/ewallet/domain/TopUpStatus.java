package th.co.truemoney.serviceinventory.ewallet.domain;

public enum TopUpStatus {
	
	AWAITING_CONFIRM("AWAITING_CONFIRM"), ORDER_VERIFIED("ORDER_VERIFIED"), PROCESSING("PROCESSING"), CONFIRMED("CONFIRMED"), FAILED("FAILED");
	
	private String topUpStatus;

	private TopUpStatus(String topUpStatus) {
		this.topUpStatus = topUpStatus;
	}

	public String getTopUpStatus() {
		return topUpStatus;
	}
	
}
