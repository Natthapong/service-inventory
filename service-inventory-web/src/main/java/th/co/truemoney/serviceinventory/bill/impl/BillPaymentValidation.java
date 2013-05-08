package th.co.truemoney.serviceinventory.bill.impl;

import java.io.Serializable;

public class BillPaymentValidation implements Serializable {

	private static final long serialVersionUID = -3121853474523696382L;
	
	private String validateDuedate;

	public String getValidateDuedate() {
		return validateDuedate;
	}
	
	public void setValidateDuedate(String validateDuedate) {
		this.validateDuedate = validateDuedate;
	}
	
}	
