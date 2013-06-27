package th.co.truemoney.serviceinventory.bill.validation;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class DebtStatusRequiredException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 4850509189718929488L;

	public DebtStatusRequiredException() {
		super(400, Code.BILL_REQUIRED_DEBT_STATUS, "bill required debt status but was null");
	}
}
