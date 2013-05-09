package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnTransferServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

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
		String accessTokenID = profileServiceClient.login(TestData.createSuccessUserLogin(), TestData.createSuccessClientLogin());
		assertNotNull(accessTokenID);

		// create transfer draft
		P2PTransferDraft p2pTransferDraft = transferServiceClient.createAndVerifyTransferDraft("0866011234", new BigDecimal("20.00"), accessTokenID);
		assertNotNull(p2pTransferDraft);
		assertNotNull(p2pTransferDraft.getID());

		// get transfer draft
		p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
		assertNotNull(p2pTransferDraft);
		assertNotNull(p2pTransferDraft.getID());
		assertNotNull(p2pTransferDraft.getMobileNumber());
		assertNotNull(p2pTransferDraft.getFullname());
		assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

		// send otp and waiting confirm
		OTP otp = transferServiceClient.requestOTP(p2pTransferDraft.getID(), accessTokenID);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		// get transfer draft and check draft status
		p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		P2PTransferDraft.Status draftStatus = transferServiceClient.verifyOTP(p2pTransferDraft.getID(), otp, accessTokenID);
		assertNotNull(draftStatus);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, draftStatus);

		// get transfer draft and check draft status
		p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, p2pTransferDraft.getStatus());

		Transaction.Status transactionStatus = transferServiceClient.performTransfer(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(Transaction.Status.VERIFIED, transactionStatus);

		// get order status
		Thread.sleep(100);
		transactionStatus = transferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessTokenID);
		assertNotNull(transactionStatus);

		// retry while processing
		while (transactionStatus == Transaction.Status.PROCESSING) {
			transactionStatus = transferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessTokenID);
			System.out.println("processing top up ...");
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(Transaction.Status.SUCCESS, transactionStatus);

		P2PTransferTransaction p2pTransaction = transferServiceClient.getTransactionResult(p2pTransferDraft.getID(), accessTokenID);

		assertNotNull(p2pTransaction);
		assertNotNull(p2pTransaction.getDraftTransaction());
		assertNotNull(p2pTransaction.getConfirmationInfo());
		assertEquals(Transaction.Status.SUCCESS, p2pTransaction.getStatus());
	}

	@Test
	public void shouldSucessResendOTP() throws InterruptedException {
		// login
				String accessTokenID = profileServiceClient.login(TestData.createSuccessUserLogin(), TestData.createSuccessClientLogin());
				assertNotNull(accessTokenID);

				// create transfer draft
				P2PTransferDraft p2pTransferDraft = transferServiceClient.createAndVerifyTransferDraft("0866011234", new BigDecimal("20.00"), accessTokenID);
				assertNotNull(p2pTransferDraft);
				assertNotNull(p2pTransferDraft.getID());

				// get transfer draft
				p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
				assertNotNull(p2pTransferDraft);
				assertNotNull(p2pTransferDraft.getID());
				assertNotNull(p2pTransferDraft.getMobileNumber());
				assertNotNull(p2pTransferDraft.getFullname());
				assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

				// send otp and waiting confirm
				OTP firstOtp = transferServiceClient.requestOTP(p2pTransferDraft.getID(), accessTokenID);
				assertNotNull(firstOtp);
				assertNotNull(firstOtp.getReferenceCode());

				// send otp and waiting confirm
				OTP secondOtp = transferServiceClient.requestOTP(p2pTransferDraft.getID(), accessTokenID);
				assertNotNull(secondOtp);
				assertNotNull(secondOtp.getReferenceCode());

				assertNotEquals(firstOtp.getReferenceCode(), secondOtp.getReferenceCode());
				assertEquals(firstOtp.getMobileNumber(), secondOtp.getMobileNumber());

				// get transfer draft and check draft status
				p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
				assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

				// confirm otp
				secondOtp.setOtpString("111111");
				P2PTransferDraft.Status draftStatus = transferServiceClient.verifyOTP(p2pTransferDraft.getID(), secondOtp, accessTokenID);
				assertNotNull(draftStatus);
				assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, draftStatus);

				// get transfer draft and check draft status
				p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
				assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, p2pTransferDraft.getStatus());

				Transaction.Status transactionStatus = transferServiceClient.performTransfer(p2pTransferDraft.getID(), accessTokenID);
				assertEquals(Transaction.Status.VERIFIED, transactionStatus);

				// get order status
				Thread.sleep(100);
				transactionStatus = transferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessTokenID);
				assertNotNull(transactionStatus);

				// retry while processing
				while (transactionStatus == Transaction.Status.PROCESSING) {
					transactionStatus = transferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessTokenID);
					System.out.println("processing top up ...");
					Thread.sleep(1000);
				}

				// retry until success
				assertEquals(Transaction.Status.SUCCESS, transactionStatus);

				P2PTransferTransaction p2pTransaction = transferServiceClient.getTransactionResult(p2pTransferDraft.getID(), accessTokenID);

				assertNotNull(p2pTransaction);
				assertNotNull(p2pTransaction.getDraftTransaction());
				assertNotNull(p2pTransaction.getConfirmationInfo());
				assertEquals(Transaction.Status.SUCCESS, p2pTransaction.getStatus());
	}
}
