package th.co.truemoney.serviceinventory.ewallet.domain;

public enum TopUpOrderStatus {

	ORDER_VERIFIED("ORDER_VERIFIED"), PROCESSING("PROCESSING"), SUCCESS("SUCCESS"), FAILED("FAILED"), BANK_FAILED("BANK_FAILED"), UMARKET_FAILED("UMARKET_FAILED");

	private String topUpStatus;

	private TopUpOrderStatus(String topUpStatus) {
		this.topUpStatus = topUpStatus;
	}

	public String getTopUpStatus() {
		return topUpStatus;
	}

}
