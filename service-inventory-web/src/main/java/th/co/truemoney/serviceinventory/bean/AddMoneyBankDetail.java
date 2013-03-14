package th.co.truemoney.serviceinventory.bean;

import java.math.BigDecimal;

public class AddMoneyBankDetail {

	private String nameEn;
	private String nameTh;
	private String logoURL;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private BigDecimal feeValue;
	private String feeType;
	private BigDecimal minTotalFee;
	private BigDecimal maxTotalFee;

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getNameTh() {
		return nameTh;
	}

	public void setNameTh(String nameTh) {
		this.nameTh = nameTh;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
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

}
