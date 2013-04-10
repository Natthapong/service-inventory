package th.co.truemoney.serviceinventory.bill.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class BillPaymentServiceImplTest {

	private BillPaymentServiceImpl billPayService;
	private OTPService otpService;
	private AccessToken accessToken;
	private AsyncBillPayProcessor asyncProcessor;

	private AccessTokenRepository accessTokenRepo = new AccessTokenMemoryRepository();
	private TransactionRepository transactionRepo = new TransactionMemoryRepository();


	@Before
	public void setup() {
		billPayService = new BillPaymentServiceImpl();

		otpService = mock(OTPService.class);

		billPayService.setAccessTokenRepo(accessTokenRepo);
		billPayService.setOtpService(otpService);
		billPayService.setTransactionRepository(transactionRepo);

		accessToken = new AccessToken("12345", "5555", "4444", "username", "0868185055", "tanathip.se@gmail.com", 41);
		accessTokenRepo.save(accessToken);

		asyncProcessor = Mockito.mock(AsyncBillPayProcessor.class);
		billPayService.setAsyncBillPayProcessor(asyncProcessor);
	}

	@Test
	public void createBillInvoice() {

		when(otpService.send(anyString())).thenReturn(new OTP("0868185055", "refCode", "******"));

		BillInvoice billInvoice = billPayService.createBillInvoice(new BillPaymentInfo(), accessToken.getAccessTokenID());

		assertNotNull(billInvoice);
		assertEquals(DraftTransaction.Status.CREATED, billInvoice.getStatus());
	}

	@Test
	public void confirmOTPTest() {

		OTP correctOTP = new OTP("0868185055", "refCode", "111111");

		BillInvoice invoice = new BillInvoice();
		invoice.setID("invoiceID");

		transactionRepo.saveBillInvoice(invoice, accessToken.getAccessTokenID());

		Status confirmation = billPayService.confirmBillInvoice(invoice.getID(), correctOTP, accessToken.getAccessTokenID());

		Assert.assertEquals(Status.OTP_CONFIRMED, confirmation);
		verify(asyncProcessor).payBill(any(BillPayment.class), any(AccessToken.class));

		BillInvoice repoInvoice = transactionRepo.getBillInvoice(invoice.getID(), accessToken.getAccessTokenID());
		Assert.assertEquals(Status.OTP_CONFIRMED, repoInvoice.getStatus());

		BillPayment billPayment = transactionRepo.getBillPayment(invoice.getID(), accessToken.getAccessTokenID());

		Assert.assertNotNull(billPayment);
		Assert.assertEquals(Transaction.Status.VERIFIED, billPayment.getStatus());
	}

	@Test
	public void sendOTP() {

		when(accessTokenRepo.getAccessToken(anyString())).thenReturn(
				new AccessToken("12345", "5555", "4444", "username",
						"0868185055", "tanathip.se@gmail.com", 41));

		when(otpService.send(anyString())).thenReturn(
				new OTP("0868185055", "12345", "string"));

		when(transactionRepo.getBillInvoice(anyString(), anyString()))
				.thenReturn(new BillInvoice());

		OTP otp = billPayService.sendOTP("12345", "111111");

		assertNotNull(otp);
	}

}
