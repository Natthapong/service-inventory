package th.co.truemoney.serviceinventory.service;

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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.impl.AsyncBillPayProcessor;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
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
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
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

        accessToken = new AccessToken("12345", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
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

        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformation("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));

    }

    @Test
    public void getBillOverDuedate() throws Exception {

        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createOverDueBillPaymentInfo();

        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

        try {
	        //when
	        @SuppressWarnings("unused")
			Bill billInformation = billPayService.retrieveBillInformation("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());
	        Assert.fail();
        } catch (ServiceInventoryWebException e) {
        	assertEquals("1012", e.getErrorCode());
        }
        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));

    }

    @Test
    public void getBillNotOverDuedate() throws Exception {

        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createNotOverDueBillPaymentInfo();

        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformation("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
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
//		assertEquals(BillPaymentDraft.Status.CREATED, bill.getStatus());
//
//		Bill repoInvoice = transactionRepo.findBill(bill.getID(), accessToken.getAccessTokenID());
//		assertNotNull(repoInvoice);
//		assertEquals(BillPaymentDraft.Status.CREATED, repoInvoice.getStatus());
    }

    @Test
    public void sendOTP() {

        //given
        BillPaymentDraft invoice = new BillPaymentDraft("invoiceID");
        transactionRepo.saveDraftTransaction(invoice, accessToken.getAccessTokenID());

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

        BillPaymentDraft invoice = new BillPaymentDraft("invoiceID", null, null, "transactionID", BillPaymentDraft.Status.OTP_SENT);
        transactionRepo.saveDraftTransaction(invoice, accessToken.getAccessTokenID());

        //when
        BillPaymentDraft.Status confirmation = billPayService.confirmBill(invoice.getID(), correctOTP, accessToken.getAccessTokenID());

        //then
        assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, confirmation);
        verify(asyncProcessor).payBill(any(BillPaymentTransaction.class), any(AccessToken.class));

        BillPaymentDraft repoInvoice = transactionRepo.findDraftTransaction(invoice.getID(), accessToken.getAccessTokenID(), BillPaymentDraft.class);
        assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, repoInvoice.getStatus());
        verify(asyncProcessor).payBill(any(BillPaymentTransaction.class), any(AccessToken.class));

        BillPaymentTransaction billPayment = transactionRepo.findTransaction(invoice.getID(), accessToken.getAccessTokenID(), BillPaymentTransaction.class);

        assertNotNull(billPayment);
        assertEquals(BillPaymentTransaction.Status.VERIFIED, billPayment.getStatus());
    }

    @Test
    public void confirmOTPFail() {

        //given
        OTP badOTP = new OTP("0868185055", "refCode", "111111");

        BillPaymentDraft invoice = new BillPaymentDraft("invoiceID", null, null, "transactionID", BillPaymentDraft.Status.OTP_SENT);
        transactionRepo.saveDraftTransaction(invoice, accessToken.getAccessTokenID());

        Mockito.doThrow(new ServiceInventoryWebException("error", "otp error")).when(otpService).isValidOTP(badOTP);

        //when
        try {
            billPayService.confirmBill(invoice.getID(), badOTP, accessToken.getAccessTokenID());
            Assert.fail();
        } catch (ServiceInventoryWebException ex) {
            Assert.assertEquals("otp error", ex.getErrorDescription());
        }

        //then
        BillPaymentDraft repoInvoice = transactionRepo.findDraftTransaction(invoice.getID(), accessToken.getAccessTokenID(), BillPaymentDraft.class);
        Assert.assertEquals(BillPaymentDraft.Status.OTP_SENT, repoInvoice.getStatus());

        try {
            transactionRepo.findTransaction(invoice.getID(), accessToken.getAccessTokenID(), BillPaymentTransaction.class);
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(Code.TRANSACTION_NOT_FOUND, ex.getErrorCode());
        }

    }

}
