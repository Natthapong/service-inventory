package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.TestServiceConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class })
public class TmnProfileServiceClientTest {
	
	@Autowired
	TmnProfileServiceClient client;

	@Test @Ignore
	public void shouldFail() {
		
		try {
			client.login(41, null);
			fail();
		} catch(ServiceInventoryException e){
			
		}
		
	}

}
