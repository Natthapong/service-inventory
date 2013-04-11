package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPaymentInfo implements Serializable {

	private static final long serialVersionUID = 6473783007140066887L;

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
	
	private BigDecimal amount;
	private ServiceFee serviceFee;
	private SourceOfFundFee[] sourceOfFundFees;
		
	public BillPaymentInfo() {
		super();
	}	
	
	public BillPaymentInfo(String target, String ref1, String ref2,	BigDecimal amount) {
		super();
		this.target = target;
		this.ref1 = ref1;
		this.ref2 = ref2;
		this.amount = amount;
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
		return amount;
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
	public SourceOfFundFee[] getSourceOfFundFees() {
		return sourceOfFundFees;
	}
	public void setSourceOfFundFees(SourceOfFundFee[] sourceOfFundFees) {
		this.sourceOfFundFees = sourceOfFundFees;
	}
	@Override
	public String toString() {
		return "BillPaymentInfo [target=" + target + ", logoURL=" + logoURL
				+ ", titleTH=" + titleTH + ", titleEN=" + titleEN
				+ ", ref1TitleTH=" + ref1TitleTH + ", ref1TitleEN="
				+ ref1TitleEN + ", ref1=" + ref1 + ", ref2TitleTH="
				+ ref2TitleTH + ", ref2TitleEN=" + ref2TitleEN + ", ref2="
				+ ref2 + ", amount=" + amount + ", serviceFee=" + serviceFee
				+ ", sourceOfFundFees=" + Arrays.toString(sourceOfFundFees)
				+ "]";
	}

}
