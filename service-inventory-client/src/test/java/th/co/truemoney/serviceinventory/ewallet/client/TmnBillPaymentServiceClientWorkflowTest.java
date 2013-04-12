package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;

public class TmnBillPaymentServiceClientWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;
	
	@Autowired
	TmnProfileServiceClient profileService;
	
	@Test
	public void shouldSuccessBillPayWorkflow() {
		// login
		String accessToken = profileService.login(41, TestData.createSuccessLogin());
		assertNotNull(accessToken);
		
		String barcode = "|010554614953100 010004552 010520120200015601 85950";
		
		BillPaymentInfo billPaymentInfo = billPaymentServiceClient.getBillInformation(barcode, accessToken);

		// TODO Complete bill pay success workflow
		
	}
}
