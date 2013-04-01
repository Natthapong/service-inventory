package th.co.truemoney.serviceinventory.ewallet.client;

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

import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class P2PTransferServiceClientWorkflowTest {

	@Autowired
	TmnTransferServiceClient transferServiceClient;

	@Autowired
	TmnProfileServiceClient profileServiceClient;
	
	@Test
	public void shouldSuccessTransferEwallet() throws InterruptedException {
		
		// login
		String accessTokenID = profileServiceClient.login(41, TestData.createSuccessLogin());
		assertNotNull(accessTokenID);
		
		// create draft transaction
		P2PDraftTransaction p2pDraftTransaction = transferServiceClient.createDraftTransaction("0866011234", new BigDecimal("20.00"), accessTokenID);
		assertNotNull(p2pDraftTransaction);
		assertNotNull(p2pDraftTransaction.getID());
		
		// get draft transaction
		p2pDraftTransaction = transferServiceClient.getDraftTransactionDetails(p2pDraftTransaction.getID(), accessTokenID);
		assertNotNull(p2pDraftTransaction);
		assertNotNull(p2pDraftTransaction.getID());
		assertNotNull(p2pDraftTransaction.getMobileNumber());
		assertNotNull(p2pDraftTransaction.getFullname());
		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

		// send otp and waiting confirm		
		OTP otp = transferServiceClient.sendOTP(p2pDraftTransaction.getID(), accessTokenID);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());		
		
		// get draft transaction and check draft status
		p2pDraftTransaction = transferServiceClient.getDraftTransactionDetails(p2pDraftTransaction.getID(), accessTokenID);
		assertEquals(DraftTransaction.Status.OTP_SENT, p2pDraftTransaction.getStatus());
		
		// confirm otp
		otp.setOtpString("111111");
		DraftTransaction.Status draftStatus = transferServiceClient.confirmDraftTransaction(p2pDraftTransaction.getID(), otp, accessTokenID);
		assertNotNull(draftStatus);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, draftStatus);

		// get draft transaction and check draft status
		p2pDraftTransaction = transferServiceClient.getDraftTransactionDetails(p2pDraftTransaction.getID(), accessTokenID);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, p2pDraftTransaction.getStatus());

		// get order status
		Thread.sleep(100);
		Transaction.Status transactionStatus = transferServiceClient.getTransactionStatus(p2pDraftTransaction.getID(), accessTokenID);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == Transaction.Status.PROCESSING) {
			transactionStatus = transferServiceClient.getTransactionStatus(p2pDraftTransaction.getID(), accessTokenID);
			System.out.println("processing top up ...");
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(Transaction.Status.SUCCESS, transactionStatus);

		P2PTransaction p2pTransaction = transferServiceClient.getTransactionResult(p2pDraftTransaction.getID(), accessTokenID);

		assertNotNull(p2pTransaction);
		assertNotNull(p2pTransaction.getDraftTransaction());
		assertNotNull(p2pTransaction.getConfirmationInfo());
		assertEquals(Transaction.Status.SUCCESS, p2pTransaction.getStatus());
	}
	
	
}
