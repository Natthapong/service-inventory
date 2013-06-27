package th.co.truemoney.serviceinventory.bill.validation;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.DebtStatus;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidation;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;

public class DebtBillValidator {

	@Autowired
	private BillPaymentValidationConfig config;

	public void validate(Bill bill) {
		
		if (hasValidateDebtStatus(bill.getTarget())) {
			
			if (bill.getDebtStatus() == null) {
				throw new DebtStatusRequiredException();
			}
			
			if (validateDebtStatus(bill.getDebtStatus())) {
				throw new DebtBillException(bill);
			}
		}
	}
	
	public void setConfig(BillPaymentValidationConfig config) {
		this.config = config;
	}

	private boolean hasValidateDebtStatus(String target) {
		 BillPaymentValidation validationRules = config.getBillValidation(target);
		 return validationRules != null && validationRules.hasValidateDeptStatus();
	}
	
	private boolean validateDebtStatus(DebtStatus debtStatus) {
		return DebtStatus.Debt.equals(debtStatus);
	}

}
