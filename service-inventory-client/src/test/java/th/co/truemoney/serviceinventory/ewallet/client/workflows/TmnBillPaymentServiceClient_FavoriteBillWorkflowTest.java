package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.ewallet.client.FavoriteServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClient_FavoriteBillWorkflowTest {

	@Autowired
	TmnBillPaymentServiceClient billPaymentServiceClient;

	@Autowired
	TransactionAuthenServiceClient authenClient;

	@Autowired
	FavoriteServicesClient favoriteClient;

	@Autowired
	TmnProfileServiceClient profileService;

	@Test
	public void shouldSuccessBillPayWorkflow() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createAdamSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		Favorite favoriteBill = TestData.createFavoriteBill();
		favoriteBill = favoriteClient.addFavorite(favoriteBill, accessToken);

        Bill bill = billPaymentServiceClient.retrieveBillInformationWithUserFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), favoriteBill.getRef2(),
                favoriteBill.getAmount(), InquiryOutstandingBillType.OFFLINE, accessToken);

		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(50);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

		// get transfer draft
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

		BillPaymentTransaction transaction = billPaymentServiceClient.getBillPaymentResult(billDraft.getID(), accessToken);

		assertNotNull(transaction);
		assertNotNull(transaction.getDraftTransaction());
		assertNotNull(transaction.getConfirmationInfo());
		assertEquals(BillPaymentTransaction.Status.SUCCESS, transaction.getStatus());

	}
	
	@Test
	public void shouldFailBillPayWorkflowAtPerformPayment() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createSimpsonsSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		Favorite favoriteBill = TestData.createFavoriteBill();
		
		favoriteBill = favoriteClient.addFavorite(favoriteBill, accessToken);

        Bill bill = billPaymentServiceClient.retrieveBillInformationWithUserFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), favoriteBill.getRef2(),
                favoriteBill.getAmount(), InquiryOutstandingBillType.OFFLINE, accessToken);

		assertNotNull(bill);
		assertNotNull(bill.getID());

		BigDecimal amount = new BigDecimal(50);
		BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

		// get transfer draft
		billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
		assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());
		try{
			billPaymentServiceClient.performPayment(billDraft.getID(), accessToken);
			fail("invalid favorite");
		} catch (ServiceInventoryException se) {
			assertEquals("1017", se.getErrorCode());
		}	
		
	}
	
	@Test
	public void shouldFailBillPayWorkflowAtAddFavorite() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createAdamSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		Favorite favoriteBill = TestData.createFavoriteBill();
		favoriteBill.setServiceCode("tx");
		
		try {
		favoriteBill = favoriteClient.addFavorite(favoriteBill, accessToken);
		fail("invalid service code");
		} catch (ServiceInventoryException se) {
			assertEquals("1018", se.getErrorCode());
		}
	}
	
	@Test
	public void shouldFailedValidateMinAmountBillPayWorkflow() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createAdamSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		Favorite favoriteBill = TestData.createFavoriteBill();
		favoriteBill = favoriteClient.addFavorite(favoriteBill, accessToken);

        Bill bill = billPaymentServiceClient.retrieveBillInformationWithUserFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), favoriteBill.getRef2(),
                favoriteBill.getAmount(), InquiryOutstandingBillType.OFFLINE, accessToken);

		assertNotNull(bill);
		assertNotNull(bill.getID());

		try {
			BigDecimal amount = new BigDecimal(0);
			billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
			fail("invalid min amount");
		} catch (ServiceInventoryException e) {
			assertEquals("20003", e.getErrorCode());
		}
		
	}
	
	@Test
	public void shouldFailedValidateMaxAmountBillPayWorkflow() throws InterruptedException {
		// login
		String accessToken = profileService.login(
				TestData.createAdamSuccessLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessToken);

		Favorite favoriteBill = TestData.createFavoriteBill();
		favoriteBill = favoriteClient.addFavorite(favoriteBill, accessToken);

        Bill bill = billPaymentServiceClient.retrieveBillInformationWithUserFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), favoriteBill.getRef2(),
                favoriteBill.getAmount(), InquiryOutstandingBillType.OFFLINE, accessToken);

		assertNotNull(bill);
		assertNotNull(bill.getID());

		try {
			BigDecimal amount = new BigDecimal("200000000");
			billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
			fail("invalid max amount");
		} catch (ServiceInventoryException e) {
			assertEquals("20003", e.getErrorCode());
		}
		
	}
	
}
