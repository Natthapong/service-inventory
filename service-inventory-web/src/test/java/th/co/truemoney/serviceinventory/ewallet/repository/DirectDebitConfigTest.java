package th.co.truemoney.serviceinventory.ewallet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreference;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.SourceOfFundPreferenceImpl;

public class DirectDebitConfigTest {
	SourceOfFundPreference directDebitConfig;

	@Before
	public void setup() {
		directDebitConfig = new SourceOfFundPreferenceImpl();
	}

	@Test
	public void testGetBankDetail() {
		 DirectDebitPreference directDebitPref = directDebitConfig.getBankPreference("SCB");
		assertNotNull(directDebitPref);
		assertEquals("The Siam Commercial Bank", directDebitPref.getBankNameEn());
	}
}
