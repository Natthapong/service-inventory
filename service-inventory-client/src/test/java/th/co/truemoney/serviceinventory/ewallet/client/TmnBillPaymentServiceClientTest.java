package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClientTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TmnProfileServiceClient client;

	String accessToken;

	@Before
	public void setup(){
		accessToken = client.login(41, TestData.createSuccessLogin());
	}

	@Test
	public void createBillInvoice(){

		String billInvoiceID = "12345";

		BillInvoice billInvoice = billPaymentServiceClient.createBillInvoice(new BillPaymentInfo(),accessToken);

		assertNotNull(billInvoice);

		OTP sentOTP = billPaymentServiceClient.sendOTP(billInvoiceID, accessToken);

		OTP userInputOTP = new OTP(sentOTP.getMobileNumber(), sentOTP.getReferenceCode(), "111111");
	}

}
