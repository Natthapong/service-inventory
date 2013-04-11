package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnTransferServiceClientTest {

	@Autowired
	TmnTransferServiceClient p2pTransferServiceClient;

	@Autowired
	TmnProfileServiceClient client;

	String accessToken;
	P2PDraftTransaction p2pDraftTransaction;

	@Before
	public void setup(){
		// login
		accessToken = client.login(41, TestData.createSuccessLogin());
		// create draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.verifyAndCreateTransferDraft("0866011234", new BigDecimal("20.00"), accessToken);

	}

	@Test
	public void createDraftTransactionSuccess() {

		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient
				.verifyAndCreateTransferDraft("0868185055", new BigDecimal(2000),
						accessToken);
		assertNotNull(p2pDraftTransaction);
		assertEquals("Target Ful***", p2pDraftTransaction.getFullname());
	}

	@Test
	public void createDraftTransactionFail() {
		try {
			p2pTransferServiceClient.verifyAndCreateTransferDraft("0868185055",
					new BigDecimal(2000), "12341235");
		} catch (ServiceInventoryException e) {
			assertNotSame("0", e.getErrorCode());
		}
	}

	@Test
	public void getDraftTransactionDetailSuccess() {
		// get draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);

		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

		// get draft transaction and check draft status
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);
		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());
		assertNotNull(p2pDraftTransaction);
	}

	@Test
	public void getDraftTransactionDetailFail() {
		try {
			p2pTransferServiceClient.getTransferDraftDetails("3", "12355");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertNotSame("0", serviceInventoryException.getErrorCode());
		}
	}

	@Test
	public void sendOTPSuccess() {

		// get draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);

		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

		OTP otp = p2pTransferServiceClient.submitTransferral(p2pDraftTransaction.getID(), accessToken);

		assertNotNull(otp);
		assertEquals("0891231234", otp.getMobileNumber());
	}

	@Test
	public void sendOTPFail() {
		try {
			p2pTransferServiceClient.submitTransferral("3", "12345");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertEquals("access token not found.",
					serviceInventoryException.getErrorDescription());
		}
	}

	@Test
	public void createTransactionSuccess() {

		// get draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);

		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

		OTP otp = p2pTransferServiceClient.submitTransferral(p2pDraftTransaction.getID(), accessToken);

		// get draft transaction and check draft status
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);
		assertEquals(DraftTransaction.Status.OTP_SENT, p2pDraftTransaction.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		DraftTransaction.Status draftStatus = p2pTransferServiceClient.verifyOTPAndPerformTransferring(p2pDraftTransaction.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED,
				draftStatus);
	}

	@Test
	public void createTransactionFail() {
		try {
			p2pTransferServiceClient.verifyOTPAndPerformTransferring("3", new OTP(
					"0868185055", "112211", "marty"), "12345");
			Assert.fail();
		} catch (ServiceInventoryException e) {
			assertEquals("access token not found.", e.getErrorDescription());
		}
	}

	@Test
	public void getTransactionStatusSuccess() {
		// get draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);

		assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

		OTP otp = p2pTransferServiceClient.submitTransferral(p2pDraftTransaction.getID(), accessToken);

		// get draft transaction and check draft status
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);
		assertEquals(DraftTransaction.Status.OTP_SENT, p2pDraftTransaction.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		DraftTransaction.Status draftStatus = p2pTransferServiceClient.verifyOTPAndPerformTransferring(p2pDraftTransaction.getID(), otp, accessToken);
		assertNotNull(draftStatus);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, draftStatus);

		Transaction.Status p2pTransactionStatus = p2pTransferServiceClient
				.getTransferingStatus(p2pDraftTransaction.getID(), accessToken);

		assertEquals(Transaction.Status.SUCCESS, p2pTransactionStatus);
	}

	@Test
	public void getTransactionStatusFail() {

		try{
			// get draft transaction
			p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);

			assertEquals(DraftTransaction.Status.CREATED, p2pDraftTransaction.getStatus());

			OTP otp = p2pTransferServiceClient.submitTransferral(p2pDraftTransaction.getID(), accessToken);

			// get draft transaction and check draft status
			p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);
			assertEquals(DraftTransaction.Status.OTP_SENT, p2pDraftTransaction.getStatus());

			// confirm otp
			otp.setOtpString("111121");
			DraftTransaction.Status draftStatus = p2pTransferServiceClient.verifyOTPAndPerformTransferring(p2pDraftTransaction.getID(), otp, accessToken);
			assertNotNull(draftStatus);
			assertEquals(DraftTransaction.Status.OTP_CONFIRMED, draftStatus);

			Transaction.Status p2pTransactionStatus = p2pTransferServiceClient
					.getTransferingStatus(p2pDraftTransaction.getID(), accessToken);
			assertNotNull(p2pTransactionStatus);
			fail("Should Fail.");
		}catch(ServiceInventoryException e){
			assertEquals("OTP not matched.",e.getErrorDescription());
		}
	}

	@Test
	public void getTransactionResultSuccess() {
		// get draft transaction
		p2pDraftTransaction = p2pTransferServiceClient.getTransferDraftDetails(p2pDraftTransaction.getID(), accessToken);
		assertEquals("Target Ful***", p2pDraftTransaction.getFullname());
	}

	@Test
	public void getTransactionResultFail() {
		// login
		String accessToken = client.login(41, TestData.createSuccessLogin());

		// create draft transaction
		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient
				.verifyAndCreateTransferDraft("0866011234", new BigDecimal("20.00"),
						accessToken);
		assertNotNull(p2pDraftTransaction);
		assertNotNull(p2pDraftTransaction.getID());

		P2PDraftTransaction transaction = p2pTransferServiceClient
				.getTransferDraftDetails(p2pDraftTransaction.getID(),
						accessToken);

		assertEquals("Target Ful***", transaction.getFullname());

	}
}
