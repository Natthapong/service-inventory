package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnTransferServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

@Category(IntegrationTest.class)
@ActiveProfiles(profiles = "local")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
public class AsyncP2PTransferServiceFailStatusTest {

	@Autowired
	private TmnTransferServiceClient transferServiceClient;

	@Autowired
	private TransactionAuthenServiceClient authenClient;

	@Autowired
	private TmnProfileServiceClient profileClient;
	
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
	public void shouldReturnActualErrorCodeAndNamespace() throws InterruptedException {

		// create transfer draft
		P2PTransferDraft p2pTransferDraft = transferServiceClient.createAndVerifyTransferDraft("0866666666", new BigDecimal("20.00"), accessTokenID);
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
		OTP otp = authenClient.requestOTP(p2pTransferDraft.getID(), accessTokenID);
		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		// get transfer draft and check draft status
		p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		P2PTransferDraft.Status draftStatus = authenClient.verifyOTP(p2pTransferDraft.getID(), otp, accessTokenID);
		assertNotNull(draftStatus);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, draftStatus);

		// get transfer draft and check draft status
		p2pTransferDraft = transferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, p2pTransferDraft.getStatus());

		Transaction.Status transactionStatus = transferServiceClient.performTransfer(p2pTransferDraft.getID(), accessTokenID);
		assertEquals(Transaction.Status.VERIFIED, transactionStatus);

		// get order status
		Thread.sleep(100);
		try {
			transactionStatus = transferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessTokenID);
		} catch (ServiceInventoryException e) {
			assertEquals("666666", e.getErrorCode());
			assertEquals("EW-CORE", e.getErrorNamespace());
		}
	}

}
