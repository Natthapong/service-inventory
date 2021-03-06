package th.co.truemoney.serviceinventory.bill.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidation;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentValidationConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class BillPaymentValidationConfigTest {

    private BillPaymentValidationConfig validationConfig;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        validationConfig = new BillPaymentValidationConfig();
    }

    @Test
    public void getBillValidation() {
        BillPaymentValidation validation = validationConfig.getBillValidation("mea");
        assertNotNull(validation);
        assertEquals(true, validation.hasValidateDuedate());
    }

 }
