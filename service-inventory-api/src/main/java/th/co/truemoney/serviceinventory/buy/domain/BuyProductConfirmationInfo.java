package th.co.truemoney.serviceinventory.buy.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BuyProductConfirmationInfo implements Serializable {

	private static final long serialVersionUID = 4321188114858681616L;
	private String transactionID;
	private String transactionDate;
	private String pin;
	private String serial;
	
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

	
	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("transactionID: ", this.getTransactionID())
				.append("transactionDate: ", this.getTransactionDate())
				.toString();
	}
	
}
