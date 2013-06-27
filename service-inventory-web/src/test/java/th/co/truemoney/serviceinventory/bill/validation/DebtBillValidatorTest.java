package th.co.truemoney.serviceinventory.bill.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.DebtStatus;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;

public class DebtBillValidatorTest {
	
	private BillValidator billValidator;
	
	private BillPaymentValidationConfig config;
	private Bill bill;
	
	@Before
	public void setup() {
		bill = new Bill();
		config = new BillPaymentValidationConfig();
		billValidator = new BillValidator();
		billValidator.setConfig(config);
	}
	
	@Test
	public void noDebt_validateSuccess() {
		bill.setTarget("dlt");
		bill.setDebtStatus(DebtStatus.NoDebt);
		billValidator.validateDebtStatus(bill);
	}
	
	@Test
	public void hasDebt_noDebtStatus_fail() {
		bill.setTarget("mea");
		bill.setDebtStatus(null);

		try {
			billValidator.validateDebtStatus(bill);
			fail();
		} catch (DebtStatusRequiredException ex) {
		}
	}

	
	@Test
	public void hasDebt_validateFail() {
		bill.setTarget("mea");
		bill.setDebtStatus(DebtStatus.Debt);

		try {
			billValidator.validateDebtStatus(bill);
			fail();
		} catch (DebtBillException ex) {
		}
	}
	
	@Test
	public void validationFail_showCorrectErrorCode() {
		bill.setTarget("mea");
		bill.setDebtStatus(DebtStatus.Debt);
		
		try {
			billValidator.validateDebtStatus(bill);
			fail();
		} catch (DebtBillException ex) {
			assertEquals("1021", ex.getErrorCode());
		}
	}
	
	@Test
	public void ignoreUnconfigBill() {
		bill.setTarget("trmv");
		bill.setDebtStatus(DebtStatus.Debt);
		
		billValidator.validateDebtStatus(bill);
	}
	
	@Test
	public void ignoreUnconfigBillNoDebt() {
		bill.setTarget("ti");
		bill.setDebtStatus(DebtStatus.NoDebt);		
		
		billValidator.validateDebtStatus(bill);
	}
}
