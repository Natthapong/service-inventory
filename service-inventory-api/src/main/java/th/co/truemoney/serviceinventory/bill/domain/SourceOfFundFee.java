package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class SourceOfFundFee implements Serializable {
	
	private static final long serialVersionUID = -7741609765959925215L;
	private String sourceType;
	private BigDecimal fee;
	private BigDecimal totalFee;
	private String feeType;
	private BigDecimal minFeeAmount = BigDecimal.ZERO;
	private BigDecimal maxFeeAmount = BigDecimal.ZERO;
		
	public SourceOfFundFee() {
		super();
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getMinFeeAmount() {
		return minFeeAmount;
	}

	public void setMinFeeAmount(BigDecimal minFeeAmount) {
		this.minFeeAmount = minFeeAmount;
	}

	public BigDecimal getMaxFeeAmount() {
		return maxFeeAmount;
	}

	public void setMaxFeeAmount(BigDecimal maxFeeAmount) {
		this.maxFeeAmount = maxFeeAmount;
	}

	@Override
	public String toString() {
		return "SourceOfFundFee [sourceType=" + sourceType + ", fee=" + fee
				+ ", totalFee=" + totalFee + ", feeType=" + feeType
				+ ", minFeeAmount=" + minFeeAmount + ", maxFeeAmount="
				+ maxFeeAmount + "]";
	}	
		
}
