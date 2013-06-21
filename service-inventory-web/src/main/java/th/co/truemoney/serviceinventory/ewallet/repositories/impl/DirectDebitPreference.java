package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.io.Serializable;
import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.util.FeeUtil;

public class DirectDebitPreference implements Serializable {

	private static final long serialVersionUID = -34657460109959227L;
	private String bankNameEn;
    private String bankNameTh;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal feeValue;
    private String feeType;
    private BigDecimal minTotalFee;
    private BigDecimal maxTotalFee;

    private transient FeeUtil feeUtil = new FeeUtil();

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
	public BigDecimal getFeeValue() {
		return feeValue;
	}
	public void setFeeValue(BigDecimal feeValue) {
		this.feeValue = feeValue;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public BigDecimal getMinTotalFee() {
		return minTotalFee;
	}
	public void setMinTotalFee(BigDecimal minTotalFee) {
		this.minTotalFee = minTotalFee;
	}
	public BigDecimal getMaxTotalFee() {
		return maxTotalFee;
	}
	public void setMaxTotalFee(BigDecimal maxTotalFee) {
		this.maxTotalFee = maxTotalFee;
	}
	public BigDecimal calculateTotalFee(BigDecimal amount) {
		return feeUtil.calculateFee(amount, getFeeValue(), getFeeType(), getMinTotalFee(), getMaxTotalFee());
	}
}
