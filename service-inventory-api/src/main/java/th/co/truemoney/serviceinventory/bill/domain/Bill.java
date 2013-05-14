package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Bill implements Serializable {

    private static final long serialVersionUID = 6473783007140066887L;

    private String ID;

    private String target;
    private String logoURL;
    private String titleTH;
    private String titleEN;

    private String ref1TitleTH;
    private String ref1TitleEN;
    private String ref1;

    private String ref2TitleTH;
    private String ref2TitleEN;
    private String ref2;

    private String partialPayment;
    private String callCenterNumber;

    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal minAmount = BigDecimal.ZERO;
    private BigDecimal maxAmount = BigDecimal.ZERO;

    private ServiceFeeInfo serviceFee;
    private SourceOfFund[] sourceOfFundFees;

    private boolean favoritable = false;
    private boolean favorited = false;
    private String payWith = "barcode";

    private Date dueDate;

    public Bill() {
        super();
    }

    public Bill(String ID, String target, String ref1, String ref2,
            BigDecimal amount) {
        this.ID = ID;
        this.target = target;
        this.ref1 = ref1;
        this.ref2 = ref2;
        this.amount = amount;
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

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
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

    public String getRef1TitleTH() {
        return ref1TitleTH;
    }

    public void setRef1TitleTH(String ref1TitleTH) {
        this.ref1TitleTH = ref1TitleTH;
    }

    public String getRef1TitleEN() {
        return ref1TitleEN;
    }

    public void setRef1TitleEN(String ref1TitleEN) {
        this.ref1TitleEN = ref1TitleEN;
    }

    public String getRef1() {
        return ref1;
    }

    public void setRef1(String ref1) {
        this.ref1 = ref1;
    }

    public String getRef2TitleTH() {
        return ref2TitleTH;
    }

    public void setRef2TitleTH(String ref2TitleTH) {
        this.ref2TitleTH = ref2TitleTH;
    }

    public String getRef2TitleEN() {
        return ref2TitleEN;
    }

    public void setRef2TitleEN(String ref2TitleEN) {
        this.ref2TitleEN = ref2TitleEN;
    }

    public String getRef2() {
        return ref2;
    }

    public void setRef2(String ref2) {
        this.ref2 = ref2;
    }

    public BigDecimal getAmount() {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getPartialPayment() {
        return partialPayment;
    }

    public void setPartialPayment(String partialPayment) {
        this.partialPayment = partialPayment;
    }

    public String getCallCenterNumber() {
        return callCenterNumber;
    }

    public void setCallCenterNumber(String callCenterNumber) {
        this.callCenterNumber = callCenterNumber;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isFavoritable() {
        return favoritable;
    }

    public void setFavoritable(boolean favoritable) {
        this.favoritable = favoritable;
    }
    
    public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public String getPayWith() {
        return payWith;
    }

    public void setPayWith(String payWith) {
        this.payWith = payWith;
    }

    @Override
    public String toString() {
        return "Bill [ID=" + ID + ", target=" + target + ", logoURL=" + logoURL
                + ", titleTH=" + titleTH + ", titleEN=" + titleEN
                + ", ref1TitleTH=" + ref1TitleTH + ", ref1TitleEN="
                + ref1TitleEN + ", ref1=" + ref1 + ", ref2TitleTH="
                + ref2TitleTH + ", ref2TitleEN=" + ref2TitleEN + ", ref2="
                + ref2 + ", partialPayment=" + partialPayment
                + ", callCenterNumber=" + callCenterNumber + ", amount="
                + amount + ", minAmount=" + minAmount + ", maxAmount="
                + maxAmount + ", serviceFee=" + serviceFee
                + ", sourceOfFundFees=" + Arrays.toString(sourceOfFundFees)
                + ", dueDate=" + dueDate + "]";
    }

}
