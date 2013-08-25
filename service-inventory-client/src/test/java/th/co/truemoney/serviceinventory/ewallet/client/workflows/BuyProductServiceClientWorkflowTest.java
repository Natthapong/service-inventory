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

import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBuyProductServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class BuyProductServiceClientWorkflowTest {

	@Autowired
	TmnBuyProductServiceClient tmnBuyProductServiceClient;

	@Autowired
	TransactionAuthenServiceClient authenClient;

	@Autowired
	TmnProfileServiceClient profileServiceClient;

	@Test
	public void shouldSuccessBuyProduct() throws InterruptedException {

		// login
		String accessTokenID = profileServiceClient.login(TestData.createSuccessUserLogin(), TestData.createSuccessClientLogin());
		assertNotNull(accessTokenID);

		// create buy product draft
		BuyProductDraft buyProductDraft = tmnBuyProductServiceClient.createAndVerifyBuyProductDraft("epin_c", "0866011234", new BigDecimal("20.00"), accessTokenID);
		assertNotNull(buyProductDraft);
		assertNotNull(buyProductDraft.getID());
		
		// get buy product draft
		buyProductDraft = tmnBuyProductServiceClient.getBuyProductDraftDetails(buyProductDraft.getID(), accessTokenID);
		assertNotNull(buyProductDraft);
		assertNotNull(buyProductDraft.getID());
		assertEquals("0866011234", buyProductDraft.getRecipientMobileNumber());
		assertEquals(BuyProductDraft.Status.CREATED, buyProductDraft.getStatus());
		
		// send otp and waiting confirm
		OTP otp = authenClient.requestOTP(buyProductDraft.getID(), accessTokenID);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());
		
		// get buy product draft and check draft status
		buyProductDraft = tmnBuyProductServiceClient.getBuyProductDraftDetails(buyProductDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_SENT, buyProductDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		BuyProductDraft.Status draftStatus = authenClient.verifyOTP(buyProductDraft.getID(), otp, accessTokenID);
		assertNotNull(draftStatus);
		assertEquals(BuyProductDraft.Status.OTP_CONFIRMED, draftStatus);

		// get buy product draft and check draft status
		buyProductDraft = tmnBuyProductServiceClient.getBuyProductDraftDetails(buyProductDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, buyProductDraft.getStatus());

		Transaction.Status transactionStatus = tmnBuyProductServiceClient.performBuyProduct(buyProductDraft.getID(), accessTokenID);
		assertEquals(Transaction.Status.VERIFIED, transactionStatus);

		// get order status
		Thread.sleep(100);
		transactionStatus = tmnBuyProductServiceClient.getBuyProductStatus(buyProductDraft.getID(), accessTokenID);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == Transaction.Status.PROCESSING) {
			transactionStatus = tmnBuyProductServiceClient.getBuyProductStatus(buyProductDraft.getID(), accessTokenID);
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(Transaction.Status.SUCCESS, transactionStatus);

		BuyProductTransaction buyProductTransaction = tmnBuyProductServiceClient.getBuyProductResult(buyProductDraft.getID(), accessTokenID);

		assertNotNull(buyProductTransaction);
		assertNotNull(buyProductTransaction.getDraftTransaction());
		assertNotNull(buyProductTransaction.getConfirmationInfo());
		assertEquals(Transaction.Status.SUCCESS, buyProductTransaction.getStatus());
		
	}

}
