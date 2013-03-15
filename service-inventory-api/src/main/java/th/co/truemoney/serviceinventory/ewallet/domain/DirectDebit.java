package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DirectDebit extends SourceOfFund implements Serializable {
	
	private static final long serialVersionUID = -4919139902060027547L;
	private String bankCode;
	private String bankNameEn;
	private String bankNameTh;
	private String bankAccountNumber;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	
	public DirectDebit() {
		super();
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankNameEn() {
		return bankNameEn;
	}

	public void setBankNameEn(String bankNameEn) {
		this.bankNameEn = bankNameEn;
	}

	public String getBankNameTh() {
		return bankNameTh;
	}

	public void setBankNameTh(String bankNameTh) {
		this.bankNameTh = bankNameTh;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
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
		.append("sourceId : ", this.getSourceOfFundId())
		.append("bankCode: ", this.bankCode)
		.append("bankNameEn: ", this.bankNameEn)
		.append("bankNameTh: ", this.bankNameTh)
		.append("bankAccountNumber: ", this.bankAccountNumber)
		.append("minAmount: ", this.minAmount)
		.append("maxAmount: ", this.maxAmount)
		.toString();
	}
	
}
