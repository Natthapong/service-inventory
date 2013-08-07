package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class,
		LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnProfileRegisterWorkflowTest {

	@Autowired
	TmnProfileServiceClient profileService;

	@Test
	public void shouldSuccessRegister() {

		// verify email
		String response = profileService.validateEmail(41,
				"tanathip.se@gmail.com");
		assertNotNull(response);
		assertEquals("tanathip.se@gmail.com", response);

		// pre-register and create otp
		OTP otp = profileService.createProfile(41, new TmnProfile(
				"user1@test.com", "12345", "Tanathip V", "9999", "0868185055",
				new BigDecimal(100), "SA", 0));
		assertNotNull(otp);
		assertEquals("0868185055", otp.getMobileNumber());
		otp.setOtpString("111111");

		// confirm create profile
		TmnProfile tmnProfile = profileService.confirmCreateProfile(41, otp);
		assertNotNull(tmnProfile);
	}

	
	@Test
	public void shouldSuccessChangePin() {
		
        String accessTokenID = profileService.login(
                TestData.createAdamSuccessLogin(),
                TestData.createSuccessClientLogin());
        
        String mobileNumber = profileService.changePin(accessTokenID, TestData.createChangePin());
        
        assertNotNull(mobileNumber);
        assertEquals("0891111111",mobileNumber);
	}
	
	@Test
	public void shouldSuccessChangeFullName() {
		
        String accessTokenID = profileService.login(
                TestData.createAdamSuccessLogin(),
                TestData.createSuccessClientLogin());
        
        TmnProfile tmnProfile = profileService.updateTruemoneyProfile(accessTokenID, TestData.createChangeTmnProfile());
        
        assertNotNull(tmnProfile);
	}

}
