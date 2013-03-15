package th.co.truemoney.serviceinventory.ewallet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;

public class DirectDebitConfigTest {
	DirectDebitConfig directDebitConfig;
	
	@Before
	public void setup() {
		directDebitConfig = new DirectDebitConfigImpl();
	}

	@Test
	public void testGetBankDetail() {
		DirectDebitConfigBean directDebit = directDebitConfig.getBankDetail("SCB"); 
		assertNotNull(directDebit);
		assertEquals("The Siam Commercial Bank", directDebit.getBankNameEn());
	}
}
