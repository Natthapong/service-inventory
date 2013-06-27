package th.co.truemoney.serviceinventory.bill.validation;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.DebtStatus;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidation;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;

public class BillValidator {

	@Autowired
    private BillPaymentValidationConfig config;

    public void validateOverDue(Bill bill) {

        if (hasValidateDuedate(bill.getTarget())) {
            if (bill.getDueDate() == null) {
                throw new BillDueDateMissingException(bill);
            }

            if (isOverDue(bill.getDueDate())) {
                throw new OverDueBillException(bill);
            }
        }
    }
    
	public void validateDebtStatus(Bill bill) {
		
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

    private boolean hasValidateDuedate(String billCode) {
        BillPaymentValidation validationRules = config.getBillValidation(billCode);
        return validationRules != null && validationRules.hasValidateDuedate();
    }

    private boolean isOverDue(Date dueDate) {
        Calendar today = resetTime(new Date());
        Calendar dueDeteCal = resetTime(dueDate);

        return today.compareTo(dueDeteCal) > 0;
    }

    private Calendar resetTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

	private boolean hasValidateDebtStatus(String target) {
		 BillPaymentValidation validationRules = config.getBillValidation(target);
		 return validationRules != null && validationRules.hasValidateDeptStatus();
	}
	
	private boolean validateDebtStatus(DebtStatus debtStatus) {
		return DebtStatus.Debt.equals(debtStatus);
	}
	
}
