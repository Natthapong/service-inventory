package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class SourceOfFund implements Serializable {

	private static final long serialVersionUID = -7741609765959925215L;
	private String sourceType;
	private BigDecimal feeRate;
	private String feeRateType;
	private BigDecimal minFeeAmount = BigDecimal.ZERO;
	private BigDecimal maxFeeAmount = BigDecimal.ZERO;

	public SourceOfFund() {
		super();
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public String getFeeRateType() {
		return feeRateType;
	}

	public void setFeeRateType(String feeRateType) {
		this.feeRateType = feeRateType;
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

	public BigDecimal calculateFee(BigDecimal amount) {
		if ("THB".equals(feeRateType)) {
			return feeRate;
		} else if ("percent".equals(feeRateType)) {
			if (amount == null) {
				return BigDecimal.ZERO;
			}

			return amount.multiply(feeRate).divide(new BigDecimal(100));
		}

		throw new ServiceInventoryException(500, "500", "unknown fee rate type: " + feeRateType, "SIENGINE");
	}

	@Override
	public String toString() {
		return "BillPaySourceOfFund [sourceType=" + sourceType + ", fee=" + feeRate
				+ ", feeType=" + feeRateType
				+ ", minFeeAmount=" + minFeeAmount + ", maxFeeAmount="
				+ maxFeeAmount + "]";
	}


}
