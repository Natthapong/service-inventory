package th.co.truemoney.serviceinventory.ewallet.client.workflows;

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

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.FavoriteServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
@ActiveProfiles(profiles = "local")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
public class BillPaymentServiceFailStatusTest {
	
	@Autowired
	FavoriteServicesClient favoriteClient;

	@Autowired
	TmnProfileServiceClient profileClient;
	
	@Autowired
	TransactionAuthenServiceClient authenClient;
	
	@Autowired
	TmnBillPaymentServiceClient billPaymentClient;
	
	private String accessTokenID;
	
	@Before
	public void setup() {
		accessTokenID = profileClient.login(
				TestData.createAdamSuccessLogin(),
				TestData.createSuccessClientLogin());
	}
	
	@After
	public void tearDown() {
		profileClient.logout(accessTokenID);
	}
	
	@Test
	public void shouldReturnActualErrorCodeAndNamespace() {
		
		String barcode = "|666666666666666 010004552 010520120200015601 85950";

		Bill bill = billPaymentClient.retrieveBillInformationWithBarcode(barcode, accessTokenID);
		
		assertNotNull(bill);

		BillPaymentDraft billDraft = billPaymentClient.verifyPaymentAbility(bill.getID(), new BigDecimal(50), accessTokenID);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// get transfer draft
		billDraft = billPaymentClient.getBillPaymentDraftDetail(billDraft.getID(), accessTokenID);
		assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

		// send otp and waiting confirm
		OTP otp = authenClient.requestOTP(billDraft.getID(), accessTokenID);
		assertNotNull(otp);

		// get transfer draft and check draft status
		billDraft = billPaymentClient.getBillPaymentDraftDetail(billDraft.getID(), accessTokenID);
		assertEquals(BillPaymentDraft.Status.OTP_SENT, billDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		BillPaymentDraft.Status draftStatus = authenClient.verifyOTP(billDraft.getID(), otp, accessTokenID);
		assertNotNull(draftStatus);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, draftStatus);
		assertNotNull(billDraft.getTransactionID());

		// get payment draft and check draft status
		billDraft = billPaymentClient.getBillPaymentDraftDetail(billDraft.getID(), accessTokenID);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

		BillPaymentTransaction.Status transactionStatus = billPaymentClient.performPayment(billDraft.getID(), accessTokenID);
		assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
		
		// get order status
		try {
			transactionStatus = billPaymentClient.getBillPaymentStatus(billDraft.getID(), accessTokenID);
			fail("status should be failed");
		} catch (ServiceInventoryException e) {
			assertEquals("666666", e.getErrorCode());
			assertEquals("UMC-SERVICE", e.getErrorNamespace());
		}
	}

}
