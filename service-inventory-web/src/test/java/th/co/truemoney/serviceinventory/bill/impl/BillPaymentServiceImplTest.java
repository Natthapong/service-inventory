package th.co.truemoney.serviceinventory.bill.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.sms.OTPService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillPaymentServiceImplTest {

	@Autowired
	private BillPaymentServiceImpl billPayService;

	@Autowired
	private AccessTokenMemoryRepository accessTokenRepo;

	@Autowired
	private TransactionMemoryRepository transactionRepo;

	private OTPService otpService;
	private AccessToken accessToken;
	private AsyncBillPayProcessor asyncProcessor;

	@Before
	public void setup() {

		accessToken = new AccessToken("12345", "5555", "4444", "username", "0868185055", "tanathip.se@gmail.com", 41);
		accessTokenRepo.save(accessToken);

		otpService = mock(OTPService.class);
		billPayService.setOtpService(otpService);

		asyncProcessor = Mockito.mock(AsyncBillPayProcessor.class);
		billPayService.setAsyncBillPayProcessor(asyncProcessor);
	}

	@After
	public void tearDown() {
		accessTokenRepo.clear();
		transactionRepo.clear();
	}

	@Test
	public void createBillInvoice() {

		when(otpService.send(anyString())).thenReturn(new OTP("0868185055", "refCode", "******"));

		BillInvoice billInvoice = billPayService.createBillInvoice(new BillPaymentInfo(), accessToken.getAccessTokenID());

		assertNotNull(billInvoice);
		assertEquals(DraftTransaction.Status.CREATED, billInvoice.getStatus());

		BillInvoice repoInvoice = transactionRepo.getBillInvoice(billInvoice.getID(), accessToken.getAccessTokenID());
		assertNotNull(repoInvoice);
		assertEquals(DraftTransaction.Status.CREATED, repoInvoice.getStatus());
	}

	@Test
	public void sendOTP() {

		//given
		BillInvoice invoice = new BillInvoice("invoiceID");
		transactionRepo.saveBillInvoice(invoice, accessToken.getAccessTokenID());

		//when
		when(otpService.send(accessToken.getMobileNumber())).thenReturn(new OTP());

		
		billPayService.sendOTP("invoiceID", accessToken.getAccessTokenID());

		//then
		verify(otpService).send(accessToken.getMobileNumber());
	}

	@Test
	public void confirmOTPSuccess() {

		//given
		OTP correctOTP = new OTP("0868185055", "refCode", "111111");

		BillInvoice invoice = new BillInvoice("invoiceID", Status.OTP_SENT);
		transactionRepo.saveBillInvoice(invoice, accessToken.getAccessTokenID());

		//when
		Status confirmation = billPayService.confirmBillInvoice(invoice.getID(), correctOTP, accessToken.getAccessTokenID());

		//then
		assertEquals(Status.OTP_CONFIRMED, confirmation);
		verify(asyncProcessor).payBill(any(BillPayment.class), any(AccessToken.class));

		BillInvoice repoInvoice = transactionRepo.getBillInvoice(invoice.getID(), accessToken.getAccessTokenID());
		assertEquals(Status.OTP_CONFIRMED, repoInvoice.getStatus());

		BillPayment billPayment = transactionRepo.getBillPayment(invoice.getID(), accessToken.getAccessTokenID());

		assertNotNull(billPayment);
		assertEquals(Transaction.Status.VERIFIED, billPayment.getStatus());
	}

	@Test
	public void confirmOTPFail() {

		//given
		OTP badOTP = new OTP("0868185055", "refCode", "111111");

		BillInvoice invoice = new BillInvoice("invoiceID", Status.OTP_SENT);
		transactionRepo.saveBillInvoice(invoice, accessToken.getAccessTokenID());

		Mockito.doThrow(new ServiceInventoryWebException("error", "otp error")).when(otpService).isValidOTP(badOTP);

		//when
		try {
			billPayService.confirmBillInvoice(invoice.getID(), badOTP, accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ServiceInventoryWebException ex) {
			Assert.assertEquals("otp error", ex.getErrorDescription());
		}

		//then
		BillInvoice repoInvoice = transactionRepo.getBillInvoice(invoice.getID(), accessToken.getAccessTokenID());
		Assert.assertEquals(Status.OTP_SENT, repoInvoice.getStatus());

		try {
			transactionRepo.getBillPayment(invoice.getID(), accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ResourceNotFoundException ex) {
			Assert.assertEquals(Code.TRANSACTION_NOT_FOUND, ex.getErrorCode());
		}

	}

}
