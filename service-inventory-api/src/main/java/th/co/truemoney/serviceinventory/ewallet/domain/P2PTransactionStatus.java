package th.co.truemoney.serviceinventory.ewallet.domain;

public enum P2PTransactionStatus {
	
	AWAITING_CONFIRM("AWAITING_CONFIRM"), ORDER_VERIFIED("ORDER_VERIFIED"), PROCESSING("PROCESSING"), CONFIRMED("CONFIRMED"), FAILED("FAILED"), UMARKET_FAILED("UMARKET_FAILED");
	
	private String p2pTransferStatus;

	private P2PTransactionStatus(String p2pTransferStatus) {
		this.p2pTransferStatus = p2pTransferStatus;
	}

	public String getP2pTransferStatus() {
		return p2pTransferStatus;
	}
	
}
