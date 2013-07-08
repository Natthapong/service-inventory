package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.ForgotPasswordServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class ForgotPasswordServiceWorkflowTest {

	@Autowired
	private ForgotPasswordServiceClient client;
	
	@Test
	public void verifyAndConfirmForgotPasswordSuccess() throws Exception {
		
		ForgotPassword forgotPassword = new ForgotPassword("xxx@tmn.com", "1212121212121");
		forgotPassword = client.createForgotPassword(40, forgotPassword);
		assertNotNull(forgotPassword);
		
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");    	
		VerifyResetPassword verifyResetPassword = client.verifyResetPassword(40, resetPassword);
		assertNotNull(verifyResetPassword);
		assertNotNull(verifyResetPassword.getOtp());
		assertEquals("0891111111", verifyResetPassword.getOtp().getMobileNumber());
		assertEquals("tokenID", verifyResetPassword.getResetPasswordID());
		
		verifyResetPassword = client.resendOTP(40, verifyResetPassword.getResetPasswordID());
		assertNotNull(verifyResetPassword);
		assertNotNull(verifyResetPassword.getOtp());
		assertEquals("0891111111", verifyResetPassword.getOtp().getMobileNumber());
		assertEquals("tokenID", verifyResetPassword.getResetPasswordID());

		OTP otp = verifyResetPassword.getOtp();
		otp.setOtpString("111111");		
		String resetPasswordID = client.confirmResetPassword(40, verifyResetPassword);
		assertNotNull(resetPasswordID);
		assertEquals("tokenID", resetPasswordID);
		
	}


}
