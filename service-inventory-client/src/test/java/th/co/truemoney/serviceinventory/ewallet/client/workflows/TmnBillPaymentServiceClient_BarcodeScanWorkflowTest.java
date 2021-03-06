package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClient_BarcodeScanWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TransactionAuthenServiceClient authenClient;

	@Autowired
	TmnProfileServiceClient profileService;

	@Test
	public void shouldSuccessBillPayWorkflow() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		String barcode = "|010554614953100 010004552 010520120200015601 85950";

		Bill bill = billPaymentServiceClient.retrieveBillInformationWithBarcode(barcode, accessToken);
		
		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(50);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// get transfer draft
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// send otp and waiting confirm
		OTP otp = authenClient.requestOTP(billDraft.getID(), accessToken);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		// get transfer draft and check draft status
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_SENT, billDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		BillPaymentDraft.Status draftStatus = authenClient.verifyOTP(billDraft.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, draftStatus);
		assertNotNull(billDraft.getTransactionID());

		// get transfer draft and check draft status
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

		BillPaymentTransaction.Status transactionStatus = billPaymentServiceClient.performPayment(billDraft.getID(), accessToken);
		assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
		// get order status
		Thread.sleep(100);
		transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == BillPaymentTransaction.Status.PROCESSING) {
			transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(BillPaymentTransaction.Status.SUCCESS, transactionStatus);

		BillPaymentTransaction p2pTransaction = billPaymentServiceClient.getBillPaymentResult(billDraft.getID(), accessToken);

		assertNotNull(p2pTransaction);
		assertNotNull(p2pTransaction.getDraftTransaction());
		assertNotNull(p2pTransaction.getConfirmationInfo());
		assertEquals(BillPaymentTransaction.Status.SUCCESS, p2pTransaction.getStatus());
		assertEquals(true, p2pTransaction.getDraftTransaction().getBillInfo().isFavoritable());
		assertEquals(true, p2pTransaction.getDraftTransaction().getBillInfo().isFavorited());
	}
	
	@Test
	public void shouldSuccessBillPayWorkflowAndFalseFavoritable() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createEveSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		String barcode = "|010554614953100 010004552 010520120200015601 85950";

		Bill bill = billPaymentServiceClient.retrieveBillInformationWithBarcode(barcode, accessToken);
		
		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(50);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// get transfer draft
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// send otp and waiting confirm
		OTP otp = authenClient.requestOTP(billDraft.getID(), accessToken);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		// get transfer draft and check draft status
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_SENT, billDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		BillPaymentDraft.Status draftStatus = authenClient.verifyOTP(billDraft.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, draftStatus);
		assertNotNull(billDraft.getTransactionID());

		// get transfer draft and check draft status
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

		BillPaymentTransaction.Status transactionStatus = billPaymentServiceClient.performPayment(billDraft.getID(), accessToken);
		assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
		// get order status
		Thread.sleep(100);
		transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == BillPaymentTransaction.Status.PROCESSING) {
			transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(BillPaymentTransaction.Status.SUCCESS, transactionStatus);

		BillPaymentTransaction p2pTransaction = billPaymentServiceClient.getBillPaymentResult(billDraft.getID(), accessToken);

		assertNotNull(p2pTransaction);
		assertNotNull(p2pTransaction.getDraftTransaction());
		assertNotNull(p2pTransaction.getConfirmationInfo());
		assertEquals(BillPaymentTransaction.Status.SUCCESS, p2pTransaction.getStatus());
		assertEquals(false, p2pTransaction.getDraftTransaction().getBillInfo().isFavoritable());
		assertEquals(false, p2pTransaction.getDraftTransaction().getBillInfo().isFavorited());
	}
}
