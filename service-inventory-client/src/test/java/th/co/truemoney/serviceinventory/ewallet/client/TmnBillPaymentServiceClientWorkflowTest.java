package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClientWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TmnProfileServiceClient profileService;

	@Test
	public void shouldSuccessBillPayWorkflow() throws InterruptedException {
		// login
		String accessToken = profileService.login(41,
				TestData.createSuccessLogin());
		assertNotNull(accessToken);

		String barcode = "|010554614953100 010004552 010520120200015601 85950";

		BillInfo billInfo = billPaymentServiceClient.getBillInformation(barcode, accessToken);

		Bill bill = billPaymentServiceClient.createBill(billInfo, accessToken);

		// get transfer draft
		bill = billPaymentServiceClient.getBillDetail(bill.getID(), accessToken);
		assertNotNull(bill);
		assertNotNull(bill.getID());
		assertNotNull(bill.getBillInfo());
		assertEquals(Bill.Status.CREATED, bill.getStatus());

		// send otp and waiting confirm
		OTP otp = billPaymentServiceClient.sendOTP(bill.getID(), accessToken);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		// get transfer draft and check draft status
		bill = billPaymentServiceClient.getBillDetail(bill.getID(), accessToken);
		assertEquals(Bill.Status.OTP_SENT, bill.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		Bill.Status draftStatus = billPaymentServiceClient.confirmBill(bill.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(Bill.Status.OTP_CONFIRMED, draftStatus);

		// get transfer draft and check draft status
		bill = billPaymentServiceClient.getBillDetail(bill.getID(), accessToken);
		assertEquals(Bill.Status.OTP_CONFIRMED, bill.getStatus());

		// get order status
		Thread.sleep(100);
		BillPayment.Status transactionStatus = billPaymentServiceClient.getBillPaymentStatus(bill.getID(), accessToken);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == BillPayment.Status.PROCESSING) {
			transactionStatus = billPaymentServiceClient.getBillPaymentStatus(bill.getID(), accessToken);
			System.out.println("processing top up ...");
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(BillPayment.Status.SUCCESS, transactionStatus);

		BillPayment p2pTransaction = billPaymentServiceClient.getBillPaymentResult(bill.getID(), accessToken);

		assertNotNull(p2pTransaction);
		assertNotNull(p2pTransaction.getDraftTransaction());
		assertNotNull(p2pTransaction.getConfirmationInfo());
		assertEquals(BillPayment.Status.SUCCESS, p2pTransaction.getStatus());
	}
}
