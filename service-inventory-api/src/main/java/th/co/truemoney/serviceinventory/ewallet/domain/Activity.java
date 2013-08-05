package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	protected Long channel;
	protected BigDecimal amount;
	protected BigDecimal serviceFee;
	protected BigDecimal sourceOfFundFee;
	protected Date transactionDate;
	
	public Activity() {
		this.amount = BigDecimal.ZERO;
		this.serviceFee = BigDecimal.ZERO;
		this.sourceOfFundFee = BigDecimal.ZERO;
	}
	
	public Activity(Long reportID, String type, Date transactionDate, String action, String ref1) {
		this.reportID = reportID;
		this.type = type;
		this.transactionDate = transactionDate;
		this.action = action;
		this.ref1 = ref1;
		this.amount = BigDecimal.ZERO;
		this.serviceFee = BigDecimal.ZERO;
		this.sourceOfFundFee = BigDecimal.ZERO;
	}
	
	public Activity(Long reportID, String type, Date transactionDate, String action, String ref1, Long channelID) {
		this(reportID, type, transactionDate, action, ref1);
		this.channel = channelID;
	}
	
	public Activity(Long reportID, Long channelID, String type, String action, String ref1, Date transactionDate) {
		this(reportID, type, transactionDate, action, ref1, channelID);
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

	public Long getChannel() {
		return channel;
	}

	public void setChannel(Long channel) {
		this.channel = channel;
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
	
	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("reportID: ", this.getReportID())
			.append("type: ", this.getType())
			.append("action: ", this.getAction())
			.append("ref1: ", this.getRef1())
			.append("channel: ", this.getChannel())
			.append("amount: ", this.getAmount())
			.append("serviceFee: ", this.getServiceFee())
			.append("sourceOfFundFee: ", this.getSourceOfFundFee())
			.append("transactionDate: ", this.getTransactionDate())
			.toString();
	}
	
}
