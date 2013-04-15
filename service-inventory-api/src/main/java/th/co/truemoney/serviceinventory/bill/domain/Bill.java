package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

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

	private ServiceFee serviceFee;
	private BillPaySourceOfFund[] sourceOfFundFees;

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

	public ServiceFee getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(ServiceFee serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BillPaySourceOfFund[] getSourceOfFundFees() {
		return sourceOfFundFees;
	}

	public void setSourceOfFundFees(BillPaySourceOfFund[] sourceOfFundFees) {
		this.sourceOfFundFees = sourceOfFundFees;
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

	@Override
	public String toString() {
		return "BillInfo [target=" + target + ", logoURL=" + logoURL
				+ ", titleTH=" + titleTH + ", titleEN=" + titleEN
				+ ", ref1TitleTH=" + ref1TitleTH + ", ref1TitleEN="
				+ ref1TitleEN + ", ref1=" + ref1 + ", ref2TitleTH="
				+ ref2TitleTH + ", ref2TitleEN=" + ref2TitleEN + ", ref2="
				+ ref2 + ", partialPayment=" + partialPayment
				+ ", callCenterNumber=" + callCenterNumber + ", amount="
				+ amount + ", minAmount=" + minAmount + ", maxAmount="
				+ maxAmount + ", serviceFee=" + serviceFee
				+ ", sourceOfFundFees=" + Arrays.toString(sourceOfFundFees)
				+ "]";
	}

}
