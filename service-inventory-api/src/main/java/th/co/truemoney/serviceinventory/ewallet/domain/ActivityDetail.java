package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivityDetail implements Serializable {

	private static final long serialVersionUID = -7169633329969307484L;

	private String groupIcon;
	private String type;
	private String action;
	private String ref1;
	private String ref2;
	private String transactionID;
	private String transactionDate;	
	private BigDecimal amount;
	private BigDecimal serviceFee;
	private BigDecimal sourceOfFundFee;
	private String sourceOfFundType;

	
	public ActivityDetail() {
		super();
	}
	public String getGroupIcon() {
		return groupIcon;
	}
	public void setGroupIcon(String groupIcon) {
		this.groupIcon = groupIcon;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getRef1() {
		return ref1;
	}
	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}
	public String getRef2() {
		return ref2;
	}
	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getSourceOfFundType() {
		return sourceOfFundType;
	}
	public void setSourceOfFundType(String sourceOfFundType) {
		this.sourceOfFundType = sourceOfFundType;
	}
	public BigDecimal getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}
	public BigDecimal getSourceOfFundFee() {
		return sourceOfFundFee;
	}
	public void setSourceOfFundFee(BigDecimal sourceOfFundFee) {
		this.sourceOfFundFee = sourceOfFundFee;
	}
	
}
