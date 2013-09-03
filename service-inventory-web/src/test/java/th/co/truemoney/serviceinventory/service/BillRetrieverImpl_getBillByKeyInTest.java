package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.bill.impl.BillRetrieverImpl;
import th.co.truemoney.serviceinventory.bill.validation.BillValidator;
import th.co.truemoney.serviceinventory.config.EnvConfig;
import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.BillInformationMemoryRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class, EnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class BillRetrieverImpl_getBillByKeyInTest {

    @Autowired
    private BillRetrieverImpl billRetriever;

    @Autowired
    private AccessTokenMemoryRepository accessTokenRepo;

    @Autowired
    private BillInformationMemoryRepository billInfoRepo;

    @Autowired
    private LegacyFacade legacyFacade;

    private AccessToken accessToken;

    private Bill stubbedBill;

    private BillPaymentHandler mockBillPaymentHandler;

    @Before
    public void setup() {

        accessToken = new AccessToken("12345", "1111", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
        accessTokenRepo.save(accessToken);

        mockBillPaymentHandler = mock(BillPaymentHandler.class);
        legacyFacade.setBillPaymentFacade(mockBillPaymentHandler);

        stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(mockBillPaymentHandler.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBill);
    }

    @Test
    public void getBillInfoByKeyIn_ReturnABill() {

        //when
        Bill bill = billRetriever.getOfflineBillInfoByKeyInBillCode("mea", "ref1", "ref2", BigDecimal.TEN, accessToken.getAccessTokenID());

        //then
        assertNotNull(bill);
    }

    @Test
    public void getBillInfoByKeyIn_ValidateOverdue() {

        BillValidator mockOverDueValidator = Mockito.mock(BillValidator.class);
        billRetriever.setValidator(mockOverDueValidator);

        //when
        Bill bill = billRetriever.getOfflineBillInfoByKeyInBillCode("mea", "ref1", "ref2", BigDecimal.TEN, accessToken.getAccessTokenID());

        //then
        Mockito.verify(mockOverDueValidator).validateOverDue(bill);
    }

    @Test
    public void getBillInfoByKeyIn_ChannelIsKeyIn() {

        //when
        Bill bill = billRetriever.getOfflineBillInfoByKeyInBillCode("mea", "ref1", "ref2", BigDecimal.TEN, accessToken.getAccessTokenID());

        //then
        assertEquals("keyin", bill.getPayWith());
    }

    @Test
    public void getBillInfoByKeyIn_BillIsSaved() {

        //when
        Bill bill = billRetriever.getOfflineBillInfoByKeyInBillCode("mea", "ref1", "ref2", BigDecimal.TEN, accessToken.getAccessTokenID());

        //then
        Bill billFromRepo = billInfoRepo.findBill(bill.getID(), accessToken.getAccessTokenID());
        assertNotNull(billFromRepo);
    }

    @Test
    public void getBillInfoByKeyInOnline_UseInquiryAmount() {

        //given
        OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
        when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

        //when
        Bill bill = billRetriever.getOnlineBillInfoByKeyInBillCode("mea", "ref1", "ref2", accessToken.getAccessTokenID());

        //then
        assertNotNull(bill.getAmount());
    }

    @Test
    public void getBillInfoByKeyInOnline_UseRef1AndRef2FromInquiry() {

        //given
        OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
        when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

        //when
        Bill bill = billRetriever.getOnlineBillInfoByKeyInBillCode("mea", "ref1", "ref2", accessToken.getAccessTokenID());

        //then
        assertEquals(stubOutstanding.getRef1(), bill.getRef1());
        assertEquals(stubOutstanding.getRef2(), bill.getRef2());
    }

    @Test
    public void getBillInfoByKeyInOnline_ValidateOverDue() {

        //given
        OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
        when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

        BillValidator mockOverDueValidator = Mockito.mock(BillValidator.class);
        billRetriever.setValidator(mockOverDueValidator);

        //when
        Bill bill = billRetriever.getOnlineBillInfoByKeyInBillCode("mea", "ref1", "ref2", accessToken.getAccessTokenID());

        //then
        Mockito.verify(mockOverDueValidator).validateOverDue(bill);
    }

    @Test
    public void getBillInfoByKeyInOnline_BillIsSaved() {

        //given
        OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
        when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

        //when
        Bill bill = billRetriever.getOnlineBillInfoByKeyInBillCode("mea", "ref1", "ref2", accessToken.getAccessTokenID());
        //then
        Bill billFromRepo = billInfoRepo.findBill(bill.getID(), accessToken.getAccessTokenID());
        assertNotNull(billFromRepo);
    }
}
