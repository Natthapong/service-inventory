package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Activity implements Serializable {

	private static final long serialVersionUID = 552116675059958627L;
	
	protected Long reportID;
	protected String type;
	protected String action;
	protected String ref1;
	protected BigDecimal amount;
	protected BigDecimal serviceFee;
	protected BigDecimal sourceOfFundFee;
	protected Date transactionDate;
	
	public Activity() {
		this.amount = BigDecimal.ZERO;
		this.serviceFee = BigDecimal.ZERO;
		this.sourceOfFundFee = BigDecimal.ZERO;
	}

	public Long getReportID() {
		return reportID;
	}

	public void setReportID(Long reportID) {
		this.reportID = reportID;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	public BigDecimal getTotalFeeAmount() {
		BigDecimal totalFeeAmount = BigDecimal.ZERO;
		totalFeeAmount = totalFeeAmount.add(this.serviceFee);
		totalFeeAmount = totalFeeAmount.add(this.sourceOfFundFee);
		return totalFeeAmount;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal totalFeeAmount = getTotalFeeAmount();
		return totalFeeAmount.add(this.amount);
	}
	
}
