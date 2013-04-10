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
	private String source;
	private BigDecimal sourceFee;
	private BigDecimal totalSourceFee;
	private String sourceFeeType;
	private BigDecimal minFeeAmount;
	private BigDecimal maxFeeAmount;
		
	private SourceOfFundFee() {
		super();
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public BigDecimal getSourceFee() {
		return sourceFee;
	}
	public void setSourceFee(BigDecimal sourceFee) {
		this.sourceFee = sourceFee;
	}
	public BigDecimal getTotalSourceFee() {
		return totalSourceFee;
	}
	public void setTotalSourceFee(BigDecimal totalSourceFee) {
		this.totalSourceFee = totalSourceFee;
	}
	public String getSourceFeeType() {
		return sourceFeeType;
	}
	public void setSourceFeeType(String sourceFeeType) {
		this.sourceFeeType = sourceFeeType;
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
		return "SourceOfFundFee [source=" + source + ", sourceFee=" + sourceFee
				+ ", totalSourceFee=" + totalSourceFee + ", sourceFeeType="
				+ sourceFeeType + ", minFeeAmount=" + minFeeAmount
				+ ", maxFeeAmount=" + maxFeeAmount + "]";
	}
		
}
