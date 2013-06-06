package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ServiceFeeInfo implements Serializable {

	private static final long serialVersionUID = 1405463484466493619L;
	private BigDecimal feeRate;
	private String feeRateType;

	public ServiceFeeInfo() {

	}

	public ServiceFeeInfo(String feeRateType, BigDecimal feeRate) {
		this.feeRate = feeRate;
		this.feeRateType = feeRateType;
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
	
	@JsonIgnore
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        	.append("feeRate", this.getFeeRate())
        	.append("feeRateType", this.getFeeRateType())
        	.toString();
    }
	
}
