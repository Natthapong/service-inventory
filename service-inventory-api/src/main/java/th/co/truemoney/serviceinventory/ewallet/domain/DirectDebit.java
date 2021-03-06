package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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

	public DirectDebit(String sourceID, String sourceType) {
		this.sourceOfFundID = sourceID;
		this.sourceOfFundType = sourceType;
	}

	public DirectDebit(String bankCode, String bankNameEn, String bankNameTh,
			String bankAccountNumber, BigDecimal minAmount, BigDecimal maxAmount) {
		this.bankCode = bankCode;
		this.bankNameEn = bankNameEn;
		this.bankNameTh = bankNameTh;
		this.bankAccountNumber = bankAccountNumber;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
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

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.appendSuper(super.toString())
				.append("bankCode: ", this.bankCode)
				.append("bankNameEn: ", this.bankNameEn)
				.append("bankNameTh: ", this.bankNameTh)
				.append("bankAccountNumber: ", this.bankAccountNumber)
				.append("minAmount: ", this.minAmount)
				.append("maxAmount: ", this.maxAmount)
				.toString();
	}

}
