package th.co.truemoney.serviceinventory.bill.validation;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;

public class BillOverDueValidatorTest {

    //unit under test
    private BillOverDueValidator validator = new BillOverDueValidator();

    private Bill bill;

    @Before
    public void setup() {
        bill = new Bill();
        validator.setConfig(new BillPaymentValidationConfig());
    }


    @Test
    public void validate_PassedDueDate_ThrowOverDueBillException() {

        bill.setTarget("mea");
        bill.setDueDate(getYesterday());

        try {
            validator.validate(bill);
            fail();
        } catch (OverDueBillException ex) {
        }
    }

    @Test
    public void validate_StillInDueDate_ThrowNoException() {

        bill.setTarget("mea");
        bill.setDueDate(getNex7Days());

        validator.validate(bill);
    }

    @Test
    public void validate_DueDateIsToday_ThrowNoException() {

        bill.setTarget("mea");
        bill.setDueDate(new Date());

        validator.validate(bill);
    }


    @Test
    public void validate_BillHasNoDueDate_ThrowBillDueDateMissingException() {

        bill.setTarget("mea");

        try {
            validator.validate(bill);
            fail();
        } catch (BillDueDateMissingException ex) {
        }
    }

    @Test
    public void validate_BillHasNoValidateDueDateRule_ThrowNoException() {

        bill.setTarget("random");

        try {
            validator.validate(bill);
        } catch (BillDueDateMissingException ex) {
            fail();
        }
    }

    private Date getNex7Days() {
        return addDays(7);
    }

    private Date getYesterday() {
        return addDays(-1);
    }

    private Date addDays(int i) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }

}
