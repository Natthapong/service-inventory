package th.co.truemoney.serviceinventory.topup.mobile;

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
import th.co.truemoney.serviceinventory.ewallet.client.TopupMobileServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TopupMobileIntegrateTest {
	
	@Autowired 
	private TmnProfileServiceClient profileService;
	
	@Autowired public TopupMobileServicesClient client;
	
	@Test
	public void verify() {
		
		String accessToken = profileService.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());
		
		
		TopUpMobileDraft topUpMobileDraft = client.verifyAndCreateTopUpMobileDraft("0868185055", new BigDecimal(500), accessToken);
		assertNotNull(topUpMobileDraft);
	}
}
