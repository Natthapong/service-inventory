package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles="local")
public class SourceOfFundServiceClientTest {

	@Autowired
	TmnSourceOfFundServiceClient sourceOfFundServiceClient;

	@Test @Ignore
	public void shouldFail() {
		try{
			sourceOfFundServiceClient.getUserDirectDebitSources("local@tmn.com", "1234");
			fail();
		}catch(ServiceInventoryException e){
			assertEquals("9999", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}

	@Test
	public void shouldSuccess(){
		List<DirectDebit> debits = sourceOfFundServiceClient.getUserDirectDebitSources("username", "12345");
		assertNotNull(debits);
		assertEquals(3, debits.size());
		assertEquals("SCB", debits.get(0).getBankCode());
	}

}
