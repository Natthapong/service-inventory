package th.co.truemoney.serviceinventory.ewallet.client;

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
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
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

	String accessTokenID;

	@Before
	public void setup(){
		accessTokenID = client.login(41, TestData.createSuccessLogin());
	}
	
	@Test
	public void getBillInformation() {
		String barcode = "|010554614953100 010004552 010520120200015601 85950";
		
		BillInfo billPaymentInfo = billPaymentServiceClient.getBillInformation(barcode, accessTokenID);
		
		assertNotNull(billPaymentInfo);
	}

	@Test
	public void createBillInvoice(){


        String billInvoiceID = "12345";

        Bill billInvoice = billPaymentServiceClient.createBill(new BillInfo("iphone","1234","1234",new BigDecimal(100)),accessTokenID);

        assertNotNull(billInvoice);

        OTP sentOTP = billPaymentServiceClient.sendOTP(billInvoiceID, accessTokenID);

        OTP userInputOTP = new OTP(sentOTP.getMobileNumber(), sentOTP.getReferenceCode(), "111111");
        
	}
	
	
}
