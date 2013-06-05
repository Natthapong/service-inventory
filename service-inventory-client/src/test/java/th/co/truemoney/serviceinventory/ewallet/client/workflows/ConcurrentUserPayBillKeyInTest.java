package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class ConcurrentUserPayBillKeyInTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;
	
	@Autowired
	TmnProfileServiceClient client;
	
	RestTemplate restTemplate;

	@Before
	public void setup(){
		restTemplate = mock(RestTemplate.class);
	}
	
	@After
	public void tearDown(){
		reset(restTemplate);
	}
	
	@Test
	public void concurrentUserPaySameBillSuccess() {
		String adamTokenID = client.login(TestData.createAdamSuccessLogin(), TestData.createSuccessClientLogin());
		
		String simsonTokenID = client.login(TestData.createSimpsonsSuccessLogin(), TestData.createSuccessClientLogin());
		
		Bill billAdam = billPaymentServiceClient.retrieveBillInformationWithKeyin("1", adamTokenID);
		assertNotNull(billAdam);
		
		Bill billSimpsons = billPaymentServiceClient.retrieveBillInformationWithKeyin("1", simsonTokenID);
		assertNotNull(billSimpsons);
		
		billAdam.setRef1("Test Data Ref1");
		billAdam.setRef2("");
		billAdam.setAmount(new BigDecimal(1234.55));	
//				
		billSimpsons.setRef1("Test Data Ref1");
		billSimpsons.setRef2("");
		billSimpsons.setAmount(new BigDecimal(1000.00));	
		
	}
	
}
