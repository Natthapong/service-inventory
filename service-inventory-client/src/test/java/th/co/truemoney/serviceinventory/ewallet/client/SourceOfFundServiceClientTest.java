package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles="local")
@Category(IntegrationTest.class)
public class SourceOfFundServiceClientTest {

	@Autowired
	TmnDirectDebitSourceOfFundServiceClient sourceOfFundServiceClient;

	@Autowired
	TmnProfileServiceClient profileClient;

	private String accessToken;

	@Before
	public void setUp() {
		accessToken = profileClient.login(41, TestData.createSuccessLogin());
	}

	@Test
	public void shouldFail() {
		try{
			sourceOfFundServiceClient.getUserDirectDebitSources("hacker", accessToken);
			Assert.fail();
		}catch(ServiceInventoryException e){
			assertEquals("401", e.getErrorCode());
			assertEquals("Unauthorized access", e.getErrorDescription());
		}
	}

	@Test
	public void shouldSuccess(){

		List<DirectDebit> debits = sourceOfFundServiceClient.getUserDirectDebitSources("local@tmn.com", accessToken);
		assertNotNull(debits);
		assertEquals(3, debits.size());
		assertEquals("SCB", debits.get(0).getBankCode());
	}

}
