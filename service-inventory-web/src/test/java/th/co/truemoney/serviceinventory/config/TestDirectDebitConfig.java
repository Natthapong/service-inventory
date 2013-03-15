package th.co.truemoney.serviceinventory.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;

public class TestDirectDebitConfig {

	@Test
	public void testGetBankDetail() {
		
		DirectDebitConfig addMoneyDirectDebitconfig = new DirectDebitConfigImpl();
		
		assertNotNull(addMoneyDirectDebitconfig.getBankDetail("SCB"));
	}

}
