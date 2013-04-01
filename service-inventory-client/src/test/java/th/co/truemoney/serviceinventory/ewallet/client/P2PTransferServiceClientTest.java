package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.math.BigDecimal;

import org.junit.Assert;
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
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class P2PTransferServiceClientTest {

	@Autowired
	TmnTransferServiceClient p2pTransferServiceClient;

	@Test
	public void createDraftTransactionSuccess(){
		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient.createDraftTransaction("0868185055", new BigDecimal(2000), "12345");
		assertNotNull(p2pDraftTransaction);
		assertEquals("fullName", p2pDraftTransaction.getFullname());
	}

	@Test
	public void createDraftTransactionFail(){
		try{
			p2pTransferServiceClient.createDraftTransaction("0868185055", new BigDecimal(2000), "12341235");
		}catch(ServiceInventoryException e){
			assertNotSame("0", e.getErrorCode());
		}
	}

	@Test
	public void getDraftTransactionDetailSuccess() {
		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient.getDraftTransactionDetails("1", "12345");
		assertNotNull(p2pDraftTransaction);
	}

	@Test
	public void getDraftTransactionDetailFail() {
		try {
			p2pTransferServiceClient.getDraftTransactionDetails("3", "12355");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertNotSame("0", serviceInventoryException.getErrorCode());
		}
	}

	@Test
	public void sendOTPSuccess() {
		OTP otp = p2pTransferServiceClient.sendOTP("1", "12345");
		assertNotNull(otp);
		assertEquals("0868185055", otp.getMobileNumber());
	}

	@Test
	public void sendOTPFail() {
		try {
			p2pTransferServiceClient.sendOTP("3", "12345");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertEquals("Can't send OTP", serviceInventoryException.getErrorDescription());
		}
	}

	@Test
	public void createTransactionSuccess(){
		DraftTransaction.Status p2pTransactionStatus = p2pTransferServiceClient.confirmDraftTransaction("3", new OTP("0868185055", "111111", "marty"), "12345");
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, p2pTransactionStatus);
	}

	@Test
	public void createTransactionFail(){
		try {
			p2pTransferServiceClient.confirmDraftTransaction("3", new OTP("0868185055", "112211", "marty"), "12345");
			Assert.fail();
		} catch (ServiceInventoryException e) {
			assertEquals("Invalide OTP.", e.getErrorDescription());
		}
	}

	@Test
	public void getTransactionStatusSuccess(){
		Transaction.Status p2pTransactionStatus = p2pTransferServiceClient.getTransactionStatus("0000", "12345");
		assertEquals(Transaction.Status.VERIFIED, p2pTransactionStatus);
	}

	@Test
	public void getTransactionStatusFail(){
		Transaction.Status p2pTransactionStatus = p2pTransferServiceClient.getTransactionStatus("0001", "12345");
		assertEquals(Transaction.Status.FAILED, p2pTransactionStatus);
	}

	@Test
	public void getTransactionResultSuccess(){
		P2PDraftTransaction transaction = p2pTransferServiceClient.getDraftTransactionDetails("0000", "12345");
		assertEquals(new BigDecimal(2500), transaction.getAmount());
	}

	@Test
	public void getTransactionResultFail(){
		P2PDraftTransaction transaction = p2pTransferServiceClient.getDraftTransactionDetails("0000", "12345");
		assertEquals("Mart FullName", transaction.getFullname());
	}
}
