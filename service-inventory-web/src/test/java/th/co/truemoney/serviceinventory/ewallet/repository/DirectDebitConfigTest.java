package th.co.truemoney.serviceinventory.ewallet.repository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
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
		DirectDebit directDebit = directDebitConfig.getBankDetail("SCB"); 
		assertNotNull(directDebit);
		assertEquals("The Siam Commercial Bank", directDebit.getBankNameEn());
	}
}
