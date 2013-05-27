package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClient_KeyinWorkflowTest {
	
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
	public void retrieveBillInformationWithKeyinSuccessCase(){

		String accessToken = client.login(TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		Bill bill = billPaymentServiceClient.retrieveBillInformationWithKeyin("1", accessToken);
		assertNotNull(bill);
		assertEquals("tcg",bill.getTarget());
		assertNotNull(bill.getRef1());
		assertNotNull(bill.getRef2());
		
		bill.setRef1("Test Data Ref1");
		bill.setRef2("Test Data Ref2");
		
		Bill billUpdated = billPaymentServiceClient.updateBillInformation(bill.getID(), bill.getRef1(), bill.getRef2(), bill.getAmount(), accessToken);
		assertNotNull(billUpdated);
		assertEquals("Test Data Ref1",billUpdated.getRef1());
		assertEquals("Test Data Ref2",billUpdated.getRef2());
		
	}

}
