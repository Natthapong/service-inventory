package th.co.truemoney.serviceinventory.topup.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TopUpMobile implements Serializable {

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
	private BillPaySourceOfFund[] sourceOfFundFees;
	
	public TopUpMobile() {
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

	public BillPaySourceOfFund[] getSourceOfFundFees() {
		return sourceOfFundFees;
	}

	public void setSourceOfFundFees(BillPaySourceOfFund[] sourceOfFundFees) {
		this.sourceOfFundFees = sourceOfFundFees;
	}
		
}