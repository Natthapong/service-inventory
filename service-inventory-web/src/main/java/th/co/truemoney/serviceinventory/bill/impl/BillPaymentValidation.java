package th.co.truemoney.serviceinventory.bill.impl;

import java.io.Serializable;

public class BillPaymentValidation implements Serializable {

    private static final long serialVersionUID = -3121853474523696382L;

    private Boolean validateDuedate;
    private Boolean validateDeptStatus;

    public Boolean hasValidateDuedate() {
        return validateDuedate;
    }

    public void setValidateDuedate(Boolean validateDuedate) {
        this.validateDuedate = validateDuedate;
    }

	public Boolean hasValidateDeptStatus() {
		return validateDeptStatus;
	}

	public void setValidateDeptStatus(Boolean validateDeptStatus) {
		this.validateDeptStatus = validateDeptStatus;
	}

}
