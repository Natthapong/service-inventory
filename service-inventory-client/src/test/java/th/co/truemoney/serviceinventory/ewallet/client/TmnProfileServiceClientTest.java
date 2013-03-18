package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.util.EncryptUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles="local")
public class TmnProfileServiceClientTest {
	
	@Autowired
	TmnProfileServiceClient client;

	@Autowired
	private EnvironmentConfig environmentConfig;
	
	String SALT = "5dc77d2e2310519a97aae050d85bec6870b4651a63447f02dfc936814067dd45a2f90e3c662f016f20dad45a2760739860af7ae92b3de00c2fd557ecbc3cc0d5";

	@Test @Ignore
	public void shouldFail() {
		
		try {
			client.login(41, null);
			fail();
		} catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
		
	}
	
	@Test
	public void checkUserProfileUrl(){
		try{
			TmnProfile tmnProfile = client.getTruemoneyProfile(41, "12345", EncryptUtil.buildHmacSignature("12345", "12345"+SALT));
			assertNotNull(tmnProfile);
			assertEquals("Firstname lastname", tmnProfile.getFullname());
		}catch (ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
	
}
