package th.co.truemoney.serviceinventory.topup.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("transactionID: ", this.getTransactionID())
				.append("transactionDate: ", this.getTransactionDate())
				.toString();
	}
	
}
