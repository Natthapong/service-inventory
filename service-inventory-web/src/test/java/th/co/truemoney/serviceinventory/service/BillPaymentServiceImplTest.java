package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

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

import th.co.truemoney.serviceinventory.authen.impl.TransactionAuthenServiceImpl;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.DebtStatus;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.bill.impl.AsyncBillPayProcessor;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.bill.validation.DebtBillException;
import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.TestEnvConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class, TestEnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class BillPaymentServiceImplTest {

    @Autowired
    private AccessTokenMemoryRepository accessTokenRepo;

    @Autowired
    private TransactionRepositoryImpl transactionRepo;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private BillPaymentServiceImpl billPayService;
    
    @Autowired
    private TransactionAuthenServiceImpl transactionAuthenService;

    @Autowired
    private LegacyFacade legacyFacade;

    private AccessToken accessToken;

    private AsyncBillPayProcessor asyncProcessor;
    
    private BillPaymentHandler billPaymentFacade;

    @Before
    public void setup() {

        accessToken = new AccessToken("12345", "1111", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
        accessTokenRepo.save(accessToken);

        billPaymentFacade = mock(BillPaymentHandler.class);
        legacyFacade.setBillPaymentFacade(billPaymentFacade);

        asyncProcessor = Mockito.mock(AsyncBillPayProcessor.class);
        billPayService.setAsyncBillPayProcessor(asyncProcessor);
    }

    @After
    public void tearDown() {
        accessTokenRepo.clear();
        transactionRepo.setExpirableMap(new MemoryExpirableMap());
    }

    @Test
    public void getBillInformationViaBarcodeScan() {

        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));

        assertEquals("barcode", billInformation.getPayWith());
    }

    @Test
    public void getBillInformationViaFavoriteOffline() {
        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithUserFavorite("xxxx", "ref1", "ref2", new BigDecimal(400), InquiryOutstandingBillType.OFFLINE, accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBillCodeInformation(any(GetBillRequest.class));

        assertEquals("favorite", billInformation.getPayWith());
    }

    @Test
    public void getBillInformationViaFavoriteOnline() {
        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        OutStandingBill stubOutstandingBill = BillPaymentStubbed.createSuccessOutStandingBill();

        //given
        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBillPaymentInfo);
        when(billPaymentFacade.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstandingBill);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithUserFavorite("xxxx", "ref1", "ref2", BigDecimal.ZERO, InquiryOutstandingBillType.ONLINE, accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBillCodeInformation(any(GetBillRequest.class));
        verify(billPaymentFacade).getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class));
        
        assertEquals("favorite", billInformation.getPayWith());
        assertEquals(billInformation.getRef1(), stubOutstandingBill.getRef1());
        assertEquals(billInformation.getRef2(), stubOutstandingBill.getRef2());
        assertEquals(billInformation.getAmount(), stubOutstandingBill.getOutStandingBalance());
    }

    @Test
    public void getBillInformationViaKeyinOffline() {
        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithKeyin("xxxx", "ref1", "ref2", BigDecimal.TEN, InquiryOutstandingBillType.OFFLINE, accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBillCodeInformation(any(GetBillRequest.class));

        assertEquals("keyin", billInformation.getPayWith());
        assertTrue(BigDecimal.TEN.compareTo(billInformation.getAmount()) == 0);
    }
    
    @Test
    public void getBillInformationViaKeyIn_TMOB() {
    	String tmvhNumber = "0897661234";
    	
    	Bill returnedBill = new Bill();
    	returnedBill.setTarget("blabla");
    	
    	OutStandingBill returnedOutstanding = new OutStandingBill();
    	returnedOutstanding.setBillCode("tmvh");
    	
    	//given
        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(returnedBill);
        when(billPaymentFacade.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(returnedOutstanding);
        
        //when
        Bill bill = billPayService.retrieveBillInformationWithKeyin("tmob", tmvhNumber, "", new BigDecimal(300), InquiryOutstandingBillType.ONLINE, accessToken.getAccessTokenID());
        
        //then
        assertNotNull(bill);
        assertEquals("tmvh", bill.getTarget());
    }

    @Test
    public void getBillOverDuedate() throws Exception {

        Bill stubbedBillPaymentInfo = BillPaymentStubbed.createOverDueBillPaymentInfo();

        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBillPaymentInfo);

        try {
            //when
            @SuppressWarnings("unused")
            Bill billInformation = billPayService.retrieveBillInformationWithBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());
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
        Bill billInformation = billPayService.retrieveBillInformationWithBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));
    }

    @Test
    public void verifyingFavoriteBillShouldSkipOTP() {

        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setPayWith("favorite");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());

        BigDecimal amount = new BigDecimal(300);
        BillPaymentDraft billInvoice = billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());

        assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billInvoice.getStatus());

    }

    @Test
    public void verifyingBarcodeBillShouldNotSkipOTP() {

        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());

        BigDecimal amount = new BigDecimal(300);
        BillPaymentDraft billInvoice = billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());

        assertEquals(BillPaymentDraft.Status.CREATED, billInvoice.getStatus());

    }

    @Test
    public void getBillHasDebt() {
        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setDebtStatus(DebtStatus.Debt);
        billInfo.setTarget("mea");
        billInfo.setDueDate(new Date());
        
        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(billInfo);
        
        try {
	        //when
	        billPayService.retrieveBillInformationWithBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());
	        fail();
        } catch(DebtBillException ex) {
        	
        }

        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));
        
    }
    
    @Test
    public void getBillHasNoDebt() {
        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setDebtStatus(DebtStatus.NoDebt);
        billInfo.setTarget("dlt");
        billInfo.setDueDate(new Date());
        
        when(billPaymentFacade.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(billInfo);
        
        billPayService.retrieveBillInformationWithBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        verify(billPaymentFacade).getBarcodeInformation(any(GetBarcodeRequest.class));
        
    }
    
    @Test(expected=ServiceInventoryWebException.class)
    public void verifyingMinAmountException() {

        Bill billInfo = BillPaymentStubbed.createFailMinBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());

        BigDecimal amount = new BigDecimal(300);
        billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());
    }
    
    @Test(expected=ServiceInventoryWebException.class)
    public void verifyingMaxAmountException() {

        Bill billInfo = BillPaymentStubbed.createFailMaxBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());

        BigDecimal amount = new BigDecimal(3000001);
        billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());
    }
    
    @Test
    public void performPaymentSuccess() throws Exception {
        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());
        
        when(billPaymentFacade.verify(any(VerifyBillPayRequest.class))).thenReturn("verifyTxnID");
        
        BigDecimal amount = new BigDecimal(300);
        BillPaymentDraft billInvoice = billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());
        assertEquals("verifyTxnID", billInvoice.getTransactionID());
        
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), DraftTransaction.class);
		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(draftTransaction, accessToken.getAccessTokenID());
		
		BillPaymentTransaction.Status transactionStatus = billPayService.performPayment(billInvoice.getID(), accessToken.getAccessTokenID());
        assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
        
        BillPaymentTransaction billPaymentTransaction = transactionRepo.findTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), BillPaymentTransaction.class);
        billPaymentTransaction.setStatus(Transaction.Status.SUCCESS);
		transactionRepo.saveTransaction(billPaymentTransaction, accessToken.getAccessTokenID());
				
		BillPaymentTransaction.Status status = billPayService.getBillPaymentStatus(billInvoice.getID(), accessToken.getAccessTokenID());
        assertEquals(BillPaymentTransaction.Status.SUCCESS, status);
    }
    
    @Test(expected=ServiceInventoryWebException.class)
    public void performPaymentFail() throws Exception {
        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());
        
        when(billPaymentFacade.verify(any(VerifyBillPayRequest.class))).thenReturn("verifyTxnID");
        
        BigDecimal amount = new BigDecimal(300);
        BillPaymentDraft billInvoice = billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());
        assertEquals("verifyTxnID", billInvoice.getTransactionID());
        
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), DraftTransaction.class);
		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(draftTransaction, accessToken.getAccessTokenID());
		
		BillPaymentTransaction.Status transactionStatus = billPayService.performPayment(billInvoice.getID(), accessToken.getAccessTokenID());
        assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
        
        BillPaymentTransaction billPaymentTransaction = transactionRepo.findTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), BillPaymentTransaction.class);
        billPaymentTransaction.setStatus(Transaction.Status.FAILED);
		transactionRepo.saveTransaction(billPaymentTransaction, accessToken.getAccessTokenID());
				
		billPayService.getBillPaymentStatus(billInvoice.getID(), accessToken.getAccessTokenID());
    }
    
    @Test(expected=ServiceInventoryException.class)
    public void performPaymentHasFailCause() throws Exception {
        Bill billInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
        billInfo.setPayWith("barcode");
        billInfoRepo.saveBill(billInfo, accessToken.getAccessTokenID());
        
        when(billPaymentFacade.verify(any(VerifyBillPayRequest.class))).thenReturn("verifyTxnID");
        
        BigDecimal amount = new BigDecimal(300);
        BillPaymentDraft billInvoice = billPayService.verifyPaymentAbility(billInfo.getID(), amount, accessToken.getAccessTokenID());
        assertEquals("verifyTxnID", billInvoice.getTransactionID());
        
		DraftTransaction draftTransaction = transactionRepo.findDraftTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), DraftTransaction.class);
		draftTransaction.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveDraftTransaction(draftTransaction, accessToken.getAccessTokenID());
		
		BillPaymentTransaction.Status transactionStatus = billPayService.performPayment(billInvoice.getID(), accessToken.getAccessTokenID());
        assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
        
        BillPaymentTransaction billPaymentTransaction = transactionRepo.findTransaction(billInvoice.getID(), accessToken.getAccessTokenID(), BillPaymentTransaction.class);
        billPaymentTransaction.setStatus(Transaction.Status.FAILED);
        billPaymentTransaction.setFailCause(new ServiceInventoryException());
		transactionRepo.saveTransaction(billPaymentTransaction, accessToken.getAccessTokenID());
				
		billPayService.getBillPaymentStatus(billInvoice.getID(), accessToken.getAccessTokenID());
     }
}
