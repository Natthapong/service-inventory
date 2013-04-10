package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;

public class BillPaymentConfirmationInfo implements Serializable {

	private static final long serialVersionUID = 4321188114858681616L;
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
