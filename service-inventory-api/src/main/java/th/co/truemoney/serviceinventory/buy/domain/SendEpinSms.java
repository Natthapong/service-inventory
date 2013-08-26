package th.co.truemoney.serviceinventory.buy.domain;

import java.io.Serializable;

public class SendEpinSms  implements Serializable {
	
	private static final long serialVersionUID = 6583010530257817543L;
	
	private String account;
	private String recipientMobileNumber;
	private String amount;
	private String pin;
	private String serial;
	private String txnID;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRecipientMobileNumber() {
		return recipientMobileNumber;
	}
	public void setRecipientMobileNumber(String recipientMobileNumber) {
		this.recipientMobileNumber = recipientMobileNumber;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getTxnID() {
		return txnID;
	}
	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
	
}
