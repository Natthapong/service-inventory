package th.co.truemoney.serviceinventory.bill.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
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

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;

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

	@Autowired
	private LegacyFacade legacyFacade;

	private OTPService otpService;
	private AccessToken accessToken;

	private AsyncBillPayProcessor asyncProcessor;

	private BillPaymentFacade billPaymentFacade;

	@Before
	public void setup() {

		accessToken = new AccessToken("12345", "5555", "4444", "username", "0868185055", "tanathip.se@gmail.com", 41);
		accessTokenRepo.save(accessToken);

		otpService = mock(OTPService.class);
		billPayService.setOtpService(otpService);

		billPaymentFacade = mock(BillPaymentFacade.class);
		legacyFacade.setBillPaymentFacade(billPaymentFacade);

		asyncProcessor = Mockito.mock(AsyncBillPayProcessor.class);
		billPayService.setAsyncBillPayProcessor(asyncProcessor);
	}

	@After
	public void tearDown() {
		accessTokenRepo.clear();
		transactionRepo.clear();
	}

	@Test
	public void getBillInformation() {

		BillInfo stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

		when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

		//when
		BillInfo billPaymentInfo = billPayService.getBillInformation("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

		//then
		assertNotNull(billPaymentInfo);
		verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));

	}


	@Test
	public void createBillInvoice() {
//
//		when(otpService.send(anyString())).thenReturn(new OTP("0868185055", "refCode", "******"));
//
//		when(billPaymentFacade.verify(any(BillRequest.class))).thenReturn(BillPaymentStubbed.createSuccessBillPaymentInfo());
//
//		Bill bill = billPayService.createBill(new BillInfo("iphone","1234","1234",new BigDecimal(100)), accessToken.getAccessTokenID());
//
//		assertNotNull(bill);
//		assertEquals(Bill.Status.CREATED, bill.getStatus());
//
//		Bill repoInvoice = transactionRepo.findBill(bill.getID(), accessToken.getAccessTokenID());
//		assertNotNull(repoInvoice);
//		assertEquals(Bill.Status.CREATED, repoInvoice.getStatus());
	}

	@Test
	public void sendOTP() {

		//given
		Bill invoice = new Bill("invoiceID");
		transactionRepo.saveBill(invoice, accessToken.getAccessTokenID());

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

		Bill invoice = new Bill("invoiceID", Bill.Status.OTP_SENT);
		transactionRepo.saveBill(invoice, accessToken.getAccessTokenID());

		//when
		Bill.Status confirmation = billPayService.confirmBill(invoice.getID(), correctOTP, accessToken.getAccessTokenID());

		//then
		assertEquals(Bill.Status.OTP_CONFIRMED, confirmation);
		verify(asyncProcessor).payBill(any(BillPayment.class), any(AccessToken.class));

		Bill repoInvoice = transactionRepo.findBill(invoice.getID(), accessToken.getAccessTokenID());
		assertEquals(Bill.Status.OTP_CONFIRMED, repoInvoice.getStatus());
		verify(asyncProcessor).payBill(any(BillPayment.class), any(AccessToken.class));

		BillPayment billPayment = transactionRepo.findBillPayment(invoice.getID(), accessToken.getAccessTokenID());

		assertNotNull(billPayment);
		assertEquals(BillPayment.Status.VERIFIED, billPayment.getStatus());
	}

	@Test
	public void confirmOTPFail() {

		//given
		OTP badOTP = new OTP("0868185055", "refCode", "111111");

		Bill invoice = new Bill("invoiceID", Bill.Status.OTP_SENT);
		transactionRepo.saveBill(invoice, accessToken.getAccessTokenID());

		Mockito.doThrow(new ServiceInventoryWebException("error", "otp error")).when(otpService).isValidOTP(badOTP);

		//when
		try {
			billPayService.confirmBill(invoice.getID(), badOTP, accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ServiceInventoryWebException ex) {
			Assert.assertEquals("otp error", ex.getErrorDescription());
		}

		//then
		Bill repoInvoice = transactionRepo.findBill(invoice.getID(), accessToken.getAccessTokenID());
		Assert.assertEquals(Bill.Status.OTP_SENT, repoInvoice.getStatus());

		try {
			transactionRepo.findBillPayment(invoice.getID(), accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ResourceNotFoundException ex) {
			Assert.assertEquals(Code.TRANSACTION_NOT_FOUND, ex.getErrorCode());
		}

	}

}
