package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import th.co.truemoney.serviceinventory.bill.validation.BillOverDueValidator;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.BillInformationMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class BillRetrieverImpl_getBillByUserFavoritedTest {

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

            accessToken = new AccessToken("12345", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
            accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
            accessTokenRepo.save(accessToken);

            mockBillPaymentHandler = mock(BillPaymentHandler.class);
            legacyFacade.setBillPaymentFacade(mockBillPaymentHandler);

            stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
            when(mockBillPaymentHandler.getBillCodeInformation(any(GetBillRequest.class))).thenReturn(stubbedBill);

            FavoriteService mockFavoriteService = mock(FavoriteService.class);
            List<Favorite> favoriteList = createMockFavoriteList();
            when(mockFavoriteService.getFavorites(Mockito.anyString())).thenReturn(favoriteList);

            billRetriever.setFavoriteService(mockFavoriteService);
        }

        private List<Favorite> createMockFavoriteList() {
            ArrayList<Favorite> list = new ArrayList<Favorite>();
            list.add(new Favorite(1L, "non-billpay", "mea", "ref1", "ref2", null));
            list.add(new Favorite(2L, "billpay", "mea", "mea-ref1", "mea-ref2", null));

            return list;
        }

        @Test
        public void getBillInfoFromUserFavorite_SupportOnlyFavoriteBill() {

            try {
                billRetriever.getOfflineBillInfoFromUserFavorited(1L, BigDecimal.TEN, accessToken.getAccessTokenID());
                fail();
            } catch(ResourceNotFoundException ex) {

            }
        }

        @Test
        public void getBillInfoFromUserFavorite_ReturnABill() {

            //when
            Bill bill = billRetriever.getOfflineBillInfoFromUserFavorited(2L, BigDecimal.TEN, accessToken.getAccessTokenID());

            //then
            assertNotNull(bill);
        }

        @Test
        public void getBillInfoFromUserFavorite_ChannelIsFavorite() {

            //when
            Bill bill = billRetriever.getOfflineBillInfoFromUserFavorited(2L, BigDecimal.TEN, accessToken.getAccessTokenID());

            //then
            assertEquals("favorite", bill.getPayWith());
        }

        @Test
        public void getBillInfoFromUserFavorite_ValidateOverDue() {

            //given
            BillOverDueValidator mockValidator = Mockito.mock(BillOverDueValidator.class);
            billRetriever.setValidator(mockValidator);

            //when
            Bill bill = billRetriever.getOfflineBillInfoFromUserFavorited(2L, BigDecimal.TEN, accessToken.getAccessTokenID());

            //then
            verify(mockValidator).validate(bill);
        }

        @Test
        public void getBillInfoFromUserFavorite_BillIsSaved() {

            //when
            Bill bill = billRetriever.getOfflineBillInfoFromUserFavorited(2L, BigDecimal.TEN, accessToken.getAccessTokenID());

            //then
            Bill billReadFromRepo = billInfoRepo.findBill(bill.getID(), accessToken.getAccessTokenID());

            assertNotNull(billReadFromRepo);
        }

        @Test
        public void getBillInfoFromUserFavoriteOnline_UseInquiryAmount() {

            //given
            OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
            when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

            //when
            Bill bill = billRetriever.getOnlineBillInfoFromUserFavorited(2L, accessToken.getAccessTokenID());

            //then
            assertNotNull(bill.getAmount());
        }

        @Test
        public void getBillInfoFromUserFavoriteOnline_UseRef1AndRef2FromInquiry() {

            //given
            OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
            when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

            //when
            Bill bill = billRetriever.getOnlineBillInfoFromUserFavorited(2L, accessToken.getAccessTokenID());

            //then
            assertEquals(stubOutstanding.getRef1(), bill.getRef1());
            assertEquals(stubOutstanding.getRef2(), bill.getRef2());
        }

        @Test
        public void getBillInfoFromUserFavoriteOnline_ValidateOverDue() {

            //given
            OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
            when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

            BillOverDueValidator mockOverDueValidator = Mockito.mock(BillOverDueValidator.class);
            billRetriever.setValidator(mockOverDueValidator);

            //when
            Bill bill = billRetriever.getOnlineBillInfoFromUserFavorited(2L, accessToken.getAccessTokenID());

            //then
            Mockito.verify(mockOverDueValidator).validate(bill);
        }

        @Test
        public void getBillInfoFromUserFavoriteOnline_BillIsSaved() {

            //given
            OutStandingBill stubOutstanding = BillPaymentStubbed.createSuccessOutStandingBill();
            when(mockBillPaymentHandler.getBillOutStandingOnline(any(InquiryOutstandingBillRequest.class))).thenReturn(stubOutstanding);

            //when
            Bill bill = billRetriever.getOnlineBillInfoFromUserFavorited(2L, accessToken.getAccessTokenID());

            //then
            Bill billFromRepo = billInfoRepo.findBill(bill.getID(), accessToken.getAccessTokenID());
            assertNotNull(billFromRepo);
        }
}
