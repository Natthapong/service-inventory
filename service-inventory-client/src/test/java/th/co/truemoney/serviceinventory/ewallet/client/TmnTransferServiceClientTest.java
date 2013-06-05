package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnTransferServiceClientTest {

	@Autowired
	TmnTransferServiceClient p2pTransferServiceClient;

	@Autowired
	TransactionAuthenServiceClient authenClient;

	@Autowired
	TmnProfileServiceClient client;

	String accessToken;
	P2PTransferDraft p2pTransferDraft;

	@Before
	public void setup(){
		// login
		accessToken = client.login(TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());
		// create transfer draft
		p2pTransferDraft = p2pTransferServiceClient.createAndVerifyTransferDraft("0866011234", new BigDecimal("20.00"), accessToken);

	}

	@Test
	public void createTransferDraftSuccess() {

		P2PTransferDraft p2pTransferDraft = p2pTransferServiceClient
				.createAndVerifyTransferDraft("0868185055", new BigDecimal(2000),
						accessToken);
		assertNotNull(p2pTransferDraft);
		assertEquals("Tar*** Ful***", p2pTransferDraft.getFullname());
	}

	@Test
	public void createTransferDraftFail() {
		try {
			p2pTransferServiceClient.createAndVerifyTransferDraft("0868185055",
					new BigDecimal(2000), "12341235");
		} catch (ServiceInventoryException e) {
			assertNotSame("0", e.getErrorCode());
		}
	}
	
	@Test
	public void setPersonalMessageSuccess(){
		p2pTransferServiceClient.setPersonalMessage(p2pTransferDraft.getID(), "test", accessToken);
	}
	
	@Test
	public void getTransferDraftDetailSuccess() {
		// get transfer draft
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);

		assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

		// get transfer draft and check draft status
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);
		assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());
		assertNotNull(p2pTransferDraft);
	}

	@Test
	public void getTransferDraftDetailFail() {
		try {
			p2pTransferServiceClient.getTransferDraftDetails("3", "12355");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertNotSame("0", serviceInventoryException.getErrorCode());
		}
	}

	@Test
	public void createTransactionSuccess() {

		// get transfer draft
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);

		assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

		OTP otp = authenClient.requestOTP(p2pTransferDraft.getID(), accessToken);

		// get transfer draft and check draft status
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);
		assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		P2PTransferDraft.Status draftStatus = authenClient.verifyOTP(p2pTransferDraft.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED,
				draftStatus);
	}

	@Test
	public void getTransactionStatusSuccess() throws Exception {

		// get transfer draft
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);

		assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

		OTP otp = authenClient.requestOTP(p2pTransferDraft.getID(), accessToken);

		// get transfer draft and check draft status
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);
		assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		P2PTransferDraft.Status draftStatus = authenClient.verifyOTP(p2pTransferDraft.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, draftStatus);

		P2PTransferTransaction.Status p2pTransactionStatus = p2pTransferServiceClient.performTransfer(p2pTransferDraft.getID(), accessToken);
		assertEquals(P2PTransferTransaction.Status.VERIFIED, p2pTransactionStatus);

		p2pTransactionStatus = p2pTransferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessToken);
		Thread.sleep(1000);

		// retry while processing
		while (p2pTransactionStatus != P2PTransferTransaction.Status.SUCCESS) {
			p2pTransactionStatus = p2pTransferServiceClient.getTransferringStatus(p2pTransferDraft.getID(), accessToken);
			Thread.sleep(1000);
		}

		assertEquals(P2PTransferTransaction.Status.SUCCESS, p2pTransactionStatus);
	}

	@Test
	public void getTransactionStatusFail() {

		try{
			// get transfer draft
			p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);

			assertEquals(P2PTransferDraft.Status.CREATED, p2pTransferDraft.getStatus());

			OTP otp = authenClient.requestOTP(p2pTransferDraft.getID(), accessToken);

			// get transfer draft and check draft status
			p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);
			assertEquals(P2PTransferDraft.Status.OTP_SENT, p2pTransferDraft.getStatus());

			// confirm otp
			otp.setOtpString("111121");
			P2PTransferDraft.Status draftStatus = authenClient.verifyOTP(p2pTransferDraft.getID(), otp, accessToken);
			assertNotNull(draftStatus);
			assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, draftStatus);

			P2PTransferTransaction.Status p2pTransactionStatus = p2pTransferServiceClient
					.getTransferringStatus(p2pTransferDraft.getID(), accessToken);
			assertNotNull(p2pTransactionStatus);
			fail("Should Fail.");
		}catch(ServiceInventoryException e){
			assertEquals("OTP not matched.",e.getErrorDescription());
		}
	}

	@Test
	public void getTransactionResultSuccess() {
		// get transfer draft
		p2pTransferDraft = p2pTransferServiceClient.getTransferDraftDetails(p2pTransferDraft.getID(), accessToken);
		assertEquals("Tar*** Ful***", p2pTransferDraft.getFullname());
	}

	@Test
	public void getTransactionResultFail() {
		// login
		String accessToken = client.login(TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		// create transfer draft
		P2PTransferDraft p2pTransferDraft = p2pTransferServiceClient
				.createAndVerifyTransferDraft("0866011234", new BigDecimal("20.00"),
						accessToken);
		assertNotNull(p2pTransferDraft);
		assertNotNull(p2pTransferDraft.getID());

		P2PTransferDraft transaction = p2pTransferServiceClient
				.getTransferDraftDetails(p2pTransferDraft.getID(),
						accessToken);

		assertEquals("Tar*** Ful***", transaction.getFullname());

	}
}
