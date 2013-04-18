package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfigTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnProfileServiceClientTest {

	@Autowired
	TmnProfileServiceClient client;

	String SALT = "5dc77d2e2310519a97aae050d85bec6870b4651a63447f02dfc936814067dd45a2f90e3c662f016f20dad45a2760739860af7ae92b3de00c2fd557ecbc3cc0d5";

	@Test
	public void shouldPassCreateProfile(){
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setEmail("tanathip.se@email.com");
		tmnProfile.setPassword("xxxxxx");
		tmnProfile.setFullname("Tanathip");
		tmnProfile.setThaiID("1212121212121");
		tmnProfile.setMobileNumber("0861234567");
		OTP otp = client.createProfile(41, tmnProfile);
		assertNotNull(otp.getReferenceCode());
	}

	@Test
	public void shouldFailCreateProfile(){
		try{
			TmnProfile tmnProfile = new TmnProfile();
			tmnProfile.setEmail("tanathip.se@email.com");
			tmnProfile.setPassword("xxxxxx");
			tmnProfile.setFullname("Tanathip");
			tmnProfile.setThaiID("1212121212121");

			client.createProfile(41, tmnProfile);
			fail();
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
		}
	}

	@Test
	public void shouldPassEmailIsExistRegistered(){
		String response = client.validateEmail(41, "tanathip.se@email.com");
		assertEquals("tanathip.se@email.com", response);
	}

	@Test
	public void shouldFailCheckEmailIsExistRegistered(){
		try {
			client.validateEmail(-1, "tanathip.se@email.com");
			fail();
		} catch (ServiceInventoryException e) {
			assertEquals("Validate error: channelID is null or empty.", e.getErrorDescription());
		}
	}

	@Test
	public void wrongUserNameShouldFail() {
		try {
			client.login(
			  new EWalletOwnerCredential("randomUsername", "hackypassword", 40),
			  TestData.createSuccessClientLogin());
			fail();
		} catch (ServiceInventoryException e) {
			assertNotSame("0", e.getErrorCode());
		}
	}

	@Test
	public void correctUsernameAndPasswordWillProduceAccessToken() {
		try {
			client.login(TestData.createSuccessUserLogin(),
					TestData.createSuccessClientLogin());
		} catch (ServiceInventoryException e) {
			fail("should not throw exception");
		}
	}

	@Test
	public void getUserProfile() {
		String accessToken = client.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		TmnProfile tmnProfile = client.getTruemoneyProfile(accessToken);
		assertNotNull(tmnProfile);
		assertEquals("username", tmnProfile.getFullname());
	}

	@Test
	public void getBalance() {
		String accessToken = client.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		BigDecimal balance = client.getEwalletBalance(accessToken);
		assertEquals(new BigDecimal("2000.00"), balance);
	}

//	@Test
//	public void checkUserProfileUrl() {
//		String url = "http://localhost:8585/service-inventory-web/v1/ewallet/profile/{accesstokenID}/{checksum}";
//		String checkSum = EncryptUtil.buildHmacSignature("12345", "12345"
//				+ SALT);
//
//			RestTemplate restTemplate = mock(RestTemplate.class);
//
//			ResponseEntity<TmnProfile> responseEntity = new ResponseEntity<TmnProfile>(new TmnProfile(), HttpStatus.OK);
//
//			when(
//					restTemplate.exchange(eq(url), eq(HttpMethod.GET),
//							any(HttpEntity.class), eq(TmnProfile.class), eq("12345"),
//							eq(checkSum))).thenReturn(responseEntity);
//
//			this.client.restTemplate = restTemplate;
//
//			TmnProfile tmnProfile = client.getTruemoneyProfile("12345");
//			assertNotNull(tmnProfile);
//
//	}
}
