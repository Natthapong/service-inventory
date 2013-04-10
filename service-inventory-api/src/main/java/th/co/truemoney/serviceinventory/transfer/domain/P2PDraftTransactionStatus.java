package th.co.truemoney.serviceinventory.transfer.domain;

public enum P2PDraftTransactionStatus {

	CREATED("CREATED"), OTP_SENT("SENT"), OTP_CONFIRMED("CONFIRMED");

	private String p2pDraftTransactionStatus;

	private P2PDraftTransactionStatus(String p2pDraftTransactionStatus) {
		this.p2pDraftTransactionStatus = p2pDraftTransactionStatus;
	}

	public String getStatus() {
		return p2pDraftTransactionStatus;
	}
	
}
