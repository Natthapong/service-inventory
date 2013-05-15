package th.co.truemoney.serviceinventory.ewallet.client.workflows.devmode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfigTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.VPNEnvironmentIntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfigTest.class})
@ActiveProfiles(profiles = "dev")
@Category(VPNEnvironmentIntegrationTest.class)
public class VPNEnvironmentTmnBillPaymentServiceClientWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TmnProfileServiceClient profileService;

	private String accessToken;
	
	@Before
	public void setUp() {
		// login
		accessToken = profileService.login(
				new EWalletOwnerCredential("kueblaw_2013@gmail.com", "33333333", 40),
				new ClientCredential("f7cb0d495ea6d989", "MOBILE_IPHONE", "IPHONE+1", "iPhone", "iPhone"));

		assertNotNull(accessToken);

	}

	@Test
	public void runTest() throws Exception {
		try {
			shouldSuccessBillPayWorkflow("|303235768500 010003357 010220120100006170 217297");
		} catch (ServiceInventoryException ex) {
			ex.printStackTrace();
		}
		
	}

	public void shouldSuccessBillPayWorkflow(String barcode) throws InterruptedException {

		Bill bill = billPaymentServiceClient.retrieveBillInformationWithBarcode(barcode, accessToken);
		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(50);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());
	}
}
