package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.util.EncryptUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
public class TmnProfileServiceClientTest {

	@Autowired
	TmnProfileServiceClient client;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	String SALT = "5dc77d2e2310519a97aae050d85bec6870b4651a63447f02dfc936814067dd45a2f90e3c662f016f20dad45a2760739860af7ae92b3de00c2fd557ecbc3cc0d5";

	@Test @Ignore
	public void shouldFail() {

		try {
			client.login(41, null);
			fail();
		} catch (ServiceInventoryException e) {
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}

	}

	@Test @Ignore
	public void getUserProfile() {
		try {
			TmnProfile tmnProfile = client.getTruemoneyProfile("12345",
					EncryptUtil.buildHmacSignature("12345", "12345" + SALT));
			assertNotNull(tmnProfile);
			assertEquals("Firstname lastname", tmnProfile.getFullname());
		} catch (ServiceInventoryException e) {
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}

	@Test @Ignore
	public void checkUserProfileUrl() {
		String url = "http://localhost:8585/service-inventory-web/v1/ewallet/getprofile/{accesstoken}/{checksum}";
		String checkSum = EncryptUtil.buildHmacSignature("12345", "12345"
				+ SALT);
		try {
			RestTemplate restTemplate = mock(RestTemplate.class);

			ResponseEntity<TmnProfile> responseEntity = new ResponseEntity<TmnProfile>(new TmnProfile(), HttpStatus.OK);

			when(
					restTemplate.exchange(eq(url), eq(HttpMethod.GET),
							any(HttpEntity.class), eq(TmnProfile.class), eq("12345"),
							eq(checkSum))).thenReturn(responseEntity);
			
			this.client.restTemplate = restTemplate;
			
			TmnProfile tmnProfile = client.getTruemoneyProfile("12345", checkSum);
			assertNotNull(tmnProfile);
			
		} catch (ServiceInventoryException e) {
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
}
