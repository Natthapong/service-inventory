package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBuyProductServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class BuyProductServiceClientWorkflowTest {

	@Autowired
	TmnBuyProductServiceClient tmnBuyProductServiceClient;

	@Autowired
	TransactionAuthenServiceClient authenClient;

	@Autowired
	TmnProfileServiceClient profileServiceClient;

	@Test
	public void shouldSuccessBuyProduct() throws InterruptedException {

		// login
		String accessTokenID = profileServiceClient.login(TestData.createSuccessUserLogin(), TestData.createSuccessClientLogin());
		assertNotNull(accessTokenID);

		// create transfer draft
		BuyProductDraft buyProductDraft = tmnBuyProductServiceClient.createAndVerifyBuyProductDraft("epin_c", "0866011234", new BigDecimal("20.00"), accessTokenID);
		assertNotNull(buyProductDraft);
		assertNotNull(buyProductDraft.getID());

	}

}
