package th.co.truemoney.serviceinventory.buy.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BuyProduct implements Serializable {

	private static final long serialVersionUID = -290742801797853300L;
	private String ID;
    private String target;
    private BigDecimal amount = BigDecimal.ZERO;
    private SourceOfFund[] sourceOfFundFees;

    public BuyProduct() {
        super();
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public SourceOfFund[] getSourceOfFundFees() {
        return sourceOfFundFees;
    }

    public void setSourceOfFundFees(SourceOfFund[] sourceOfFundFees) {
        this.sourceOfFundFees = (sourceOfFundFees != null) ? sourceOfFundFees.clone() : null;
    }

    public SourceOfFund getEwalletSourceOfFund() {
        if (sourceOfFundFees != null) {
            for (SourceOfFund sof : sourceOfFundFees) {
                if ("EW".equals(sof.getSourceType())) {
                    return sof;
                }
            }
        }
        return null;
    }

    @JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("ID: ", this.getID())
				.append("target: ", this.getTarget())
				.append("amount: ", this.getAmount())
				.append("sourceOfFundFees.length: ", this.getSourceOfFundFees()==null?0:this.getSourceOfFundFees().length)
				.toString();
	}
    
}
