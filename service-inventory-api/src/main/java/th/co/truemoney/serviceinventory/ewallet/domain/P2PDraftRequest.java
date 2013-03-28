package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class P2PDraftRequest implements Serializable {

	private static final long serialVersionUID = 3809434703107475320L;
	private String mobileNumber;	
	private BigDecimal amount;
	
	public P2PDraftRequest() {
	}
	
	public P2PDraftRequest(String mobileNumber, BigDecimal amount) {
		super();
		this.mobileNumber = mobileNumber;
		this.amount = amount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
