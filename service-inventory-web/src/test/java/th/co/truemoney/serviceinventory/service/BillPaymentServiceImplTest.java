package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.bill.impl.AsyncBillPayProcessor;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
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
    private TransactionRepositoryImpl transactionRepo;

    @Autowired
    private BillInformationRepository billInfoRepo;

    @Autowired
    private LegacyFacade legacyFacade;

    private AccessToken accessToken;

    private AsyncBillPayProcessor asyncProcessor;

    private BillPaymentHandler billPaymentFacade;

    @Before
    public void setup() {

        accessToken = new AccessToken("12345", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
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
    public void getBillInformationViaFavorite() {
    	Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithBillCode("xxxx", "ref1", "ref2", new BigDecimal(400), accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBillCodeInformation(any(GetBillRequest.class));

        assertEquals("favorite", billInformation.getPayWith());
    }
    
    @Test
    public void getBillInformationViaKeyin() {
    	Bill stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billPaymentFacade.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBillPaymentInfo);

        //when
        Bill billInformation = billPayService.retrieveBillInformationWithKeyin("xxxx", accessToken.getAccessTokenID());

        //then
        assertNotNull(billInformation);
        verify(billPaymentFacade).getBillCodeInformation(any(GetBillRequest.class));

        assertEquals("keyin", billInformation.getPayWith());
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


}
