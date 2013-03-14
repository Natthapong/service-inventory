package th.co.truemoney.serviceinventory.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.repositories.AddMoneyDirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.AddMoneyDirectDebitConfigImpl;

public class TestDirectDebitConfig {

	@Test
	public void testGetBankDetail() {
		
		AddMoneyDirectDebitConfig addMoneyDirectDebitconfig = new AddMoneyDirectDebitConfigImpl();
		
		assertNotNull(addMoneyDirectDebitconfig.getBankDetail("bankcode001"));
	}

}
