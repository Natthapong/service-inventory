package th.co.truemoney.serviceinventory.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreferenceImpl;

public class TestDirectDebitConfig {

	@Test
	public void testGetBankDetail() {

		SourceOfFundPreference addMoneyDirectDebitconfig = new SourceOfFundPreferenceImpl();

		assertNotNull(addMoneyDirectDebitconfig.getBankPreference("SCB"));
	}

}
