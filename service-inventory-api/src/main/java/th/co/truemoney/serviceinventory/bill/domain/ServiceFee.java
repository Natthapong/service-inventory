package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ServiceFee implements Serializable {

	private static final long serialVersionUID = 1405463484466493619L;
	private BigDecimal fee;
	private String feeType;
	private BigDecimal totalFee;
	private BigDecimal minFeeAmount = BigDecimal.ZERO;
	private BigDecimal maxFeeAmount = BigDecimal.ZERO;
	
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public BigDecimal getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
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
		
}
