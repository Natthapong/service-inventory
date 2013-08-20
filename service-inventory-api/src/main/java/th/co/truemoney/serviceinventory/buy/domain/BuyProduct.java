package th.co.truemoney.serviceinventory.buy.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BuyProduct implements Serializable {

    private static final long serialVersionUID = -3008611520827994960L;

    private String ID;

    private String logo;
    private String titleTH;
    private String titleEN;
    private String target;
    private String mobileNumber;
    private BigDecimal remainBalance = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal minAmount = BigDecimal.ZERO;
    private BigDecimal maxAmount = BigDecimal.ZERO;

    private ServiceFeeInfo serviceFee;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitleTH() {
        return titleTH;
    }

    public void setTitleTH(String titleTH) {
        this.titleTH = titleTH;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getRemainBalance() {
        return remainBalance;
    }

    public void setRemainBalance(BigDecimal remainBalance) {
        this.remainBalance = remainBalance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public ServiceFeeInfo getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(ServiceFeeInfo serviceFee) {
        this.serviceFee = serviceFee;
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
				.append("logo: ", this.getLogo())
				.append("titleTH: ", this.getTitleTH())
				.append("titleEN: ", this.getTitleEN())
				.append("target: ", this.getTarget())
				.append("mobileNumber: ", this.getMobileNumber())
				.append("remainBalance: ", this.getRemainBalance())
				.append("amount: ", this.getAmount())
				.append("minAmount: ", this.getMinAmount())
				.append("maxAmount: ", this.getMaxAmount())
				.append("ServiceFeeInfo: ", this.getServiceFee())
				.append("sourceOfFundFees.length: ", this.getSourceOfFundFees()==null?0:this.getSourceOfFundFees().length)
				.toString();
	}
    
}
