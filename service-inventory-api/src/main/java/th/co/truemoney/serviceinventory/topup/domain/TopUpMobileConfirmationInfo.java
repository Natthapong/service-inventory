package th.co.truemoney.serviceinventory.topup.domain;

import java.io.Serializable;

public class TopUpMobileConfirmationInfo implements Serializable {

	private static final long serialVersionUID = -1630868137095703979L;
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
