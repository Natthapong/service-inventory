package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import org.junit.Ignore;
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
	@Ignore
	public void verifyAndConfirmForgotPasswordSuccess() throws Exception {
		
    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
		VerifyResetPassword verifyResetPassword = client.verifyResetPassword(40, resetPassword);
		
		
	}


}
