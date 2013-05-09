package th.co.truemoney.serviceinventory.bill.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
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
		assertEquals("TRUE", validation.getValidateDuedate());
	}
	
	@Test
	public void isOverdue() throws Exception {
		BillPaymentServiceImpl serviceImpl = new BillPaymentServiceImpl();
		Date dueDate = new SimpleDateFormat("ddMMyy", new Locale("TH","th")).parse("310153");
		boolean isOverDue = serviceImpl.isOverdue(dueDate);
		Assert.assertTrue(isOverDue);
	}
	
}
