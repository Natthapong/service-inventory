package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DirectDebit extends SourceOfFund implements Serializable {
	
	private static final long serialVersionUID = -4919139902060027547L;
	private String bankCode;
	private String bankName;
	private String bankAccountNumber;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
		
	public DirectDebit() {
		super();
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("sourceId : ", this.getSourceId())
		.append("bankCode: ", this.bankCode)
		.append("bankName: ", this.bankName)
		.append("bankAccountNumber: ", this.bankAccountNumber)
		.append("minAmount: ", this.minAmount)
		.append("maxAmount: ", this.maxAmount)
		.toString();
	}
	
}
