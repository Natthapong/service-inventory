package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class TopUpConfirmationInfo implements Serializable {

	private static final long serialVersionUID = 4534967612568538297L;
	private String transactionID;
	private String transactionDate;
	
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	
}
