package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles="local")
public class SourceOfFundServiceClientTest {

	@Autowired
	SourceOfFundServiceClient sourceOfFundServiceClient;
	
	@Test 
	public void shouldFail() {
		try{
			sourceOfFundServiceClient.getUserDirectDebitSources("mart", "1234");
			fail();
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}

	@Test 
	public void shouldSuccess(){
		List<DirectDebit> debits = sourceOfFundServiceClient.getUserDirectDebitSources("user1.test.v1@gmail.com", "12345");
		assertNotNull(debits);
		assertEquals(1, debits.size());
		assertEquals("SCB", debits.get(0).getBankCode());
	}

}
