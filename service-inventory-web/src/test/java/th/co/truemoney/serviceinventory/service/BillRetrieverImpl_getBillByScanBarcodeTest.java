package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import th.co.truemoney.serviceinventory.bill.impl.BillRetrieverImpl;
import th.co.truemoney.serviceinventory.bill.validation.BillValidator;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.BillInformationMemoryRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class BillRetrieverImpl_getBillByScanBarcodeTest {

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
        when(mockBillPaymentHandler.getBarcodeInformation(any(GetBarcodeRequest.class))).thenReturn(stubbedBill);
    }

    @Test
    public void getBillByScanBarcode_BillPaymentHandlerHasBeenCalled() {

        //when
        Bill bill = billRetriever.getOfflineBillInfoByScanningBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertNotNull(bill);
        verify(mockBillPaymentHandler).getBarcodeInformation(any(GetBarcodeRequest.class));
    }

    @Test
    public void getBillByScanBarcode_channelIsBarcode() {

        //when
        Bill billInformation = billRetriever.getOfflineBillInfoByScanningBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        assertEquals("barcode", billInformation.getPayWith());
    }

    @Test
    public void getBillByScanBarcode_overDueIsValidated() {

        //given
        BillValidator mockValidator = Mockito.mock(BillValidator.class);
        billRetriever.setValidator(mockValidator);

        //when
        Bill billInformation = billRetriever.getOfflineBillInfoByScanningBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        verify(mockValidator).validateOverDue(billInformation);
    }

    @Test
    public void getBillScanBarcode_BillIsSaved() {

        //when
        Bill billInformation = billRetriever.getOfflineBillInfoByScanningBarcode("|010554614953100 010004552 010520120200015601 85950", accessToken.getAccessTokenID());

        //then
        Bill billReadFromRepo = billInfoRepo.findBill(billInformation.getID(), accessToken.getAccessTokenID());

        assertNotNull(billReadFromRepo);
    }


}
