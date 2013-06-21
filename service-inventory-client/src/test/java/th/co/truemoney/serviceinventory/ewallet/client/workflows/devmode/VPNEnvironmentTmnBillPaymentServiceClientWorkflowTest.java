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
import th.co.truemoney.serviceinventory.ewallet.client.TopupMobileServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfigTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.VPNEnvironmentIntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfigTest.class})
@ActiveProfiles(profiles = "dev")
@Category(VPNEnvironmentIntegrationTest.class)
public class VPNEnvironmentTmnBillPaymentServiceClientWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TmnProfileServiceClient profileService;

	@Autowired
	public TopupMobileServicesClient topupMobileServicesClient;

	@Autowired
	public TransactionAuthenServiceClient authenClient;
	
	private String accessToken;
	
	@Before
	public void setUp() {
		// login
		accessToken = profileService.login(
				new EWalletOwnerCredential("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847", 40),
				new ClientCredential("f7cb0d495ea6d989", "MOBILE_IPHONE", "IPHONE+1", "IOS_APP", "IOS_APP"));

		assertNotNull(accessToken);

	}

	@Test
	public void runTest() throws Exception {
		try {
			//shouldSuccessBillPayWorkflow("|303235768500 010003357 010220120100006170 217297");
			successTopUpMobile();
		} catch (ServiceInventoryException ex) {
			ex.printStackTrace();
		}
		
	}

	public void shouldSuccessBillPayWorkflow(String barcode) throws InterruptedException {

		Bill bill = billPaymentServiceClient.retrieveBillInformationWithBarcode(barcode, accessToken);
		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(10000);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());
	}
	
	public void successTopUpMobile() throws InterruptedException{

		TopUpMobileDraft topUpMobileDraft = topupMobileServicesClient.verifyAndCreateTopUpMobileDraft("0864041515", new BigDecimal(1000), accessToken);
		assertNotNull(topUpMobileDraft);

		OTP otp = authenClient.requestOTP(topUpMobileDraft.getID() , accessToken);
		assertNotNull(otp);

		otp.setOtpString("123456");
		DraftTransaction.Status transactionStatus = authenClient.verifyOTP(topUpMobileDraft.getID(), otp, accessToken);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, transactionStatus);

		topupMobileServicesClient.performTopUpMobile(topUpMobileDraft.getID(), accessToken);

		Thread.sleep(100);
		TopUpMobileTransaction.Status status = topupMobileServicesClient.getTopUpMobileStatus(topUpMobileDraft.getID(), accessToken);
		assertNotNull(status);

		while (status == TopUpMobileTransaction.Status.PROCESSING) {
			status = topupMobileServicesClient.getTopUpMobileStatus(topUpMobileDraft.getID(), accessToken);
			Thread.sleep(1000);
		}

		assertEquals(TopUpMobileTransaction.Status.SUCCESS, status);

		TopUpMobileTransaction topUpMobileTransaction = topupMobileServicesClient.getTopUpMobileResult(topUpMobileDraft.getID(), accessToken);
		assertNotNull(topUpMobileTransaction);
		assertNotNull(topUpMobileTransaction.getConfirmationInfo().getTransactionID());
		assertNotNull(topUpMobileTransaction.getConfirmationInfo().getTransactionDate());
	
	}

}
