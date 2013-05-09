package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;

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

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpMobileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpMobileFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class TopUpMobileServiceImplTest {

    private AccessTokenMemoryRepository accessTokenRepo;

    private TransactionRepositoryImpl transactionRepo;

    private AccessToken accessToken;

    private TopUpMobileDraft topUpMobileDraft;

    private TopUpMobile topUpMobile;

    @Autowired
    private TopUpMobileServiceImpl topUpMobileServiceImpl;

    @Autowired
    private LegacyFacade legacyFacade;

    @Autowired
    private OTPService otpService;

    private TopUpMobileFacade mockTopUpMobileFacade;

    @Before
    public void setup() {

        accessTokenRepo = new AccessTokenMemoryRepository();
        transactionRepo = new TransactionRepositoryImpl(new MemoryExpirableMap());

        accessToken = new AccessToken("12345", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
        accessTokenRepo.save(accessToken);

        topUpMobile = new TopUpMobile();
        topUpMobileDraft = new TopUpMobileDraft("7788",topUpMobile);
        transactionRepo.saveDraftTransaction(topUpMobileDraft, accessToken.getAccessTokenID());

        mockTopUpMobileFacade = Mockito.mock(TopUpMobileFacade.class);
        otpService = Mockito.mock(OTPService.class);

        topUpMobileServiceImpl.setAccessTokenRepo(accessTokenRepo);
        topUpMobileServiceImpl.setTransactionRepo(transactionRepo);
        topUpMobileServiceImpl.setOtpService(otpService);

        legacyFacade.setTopUpMobileFacade(mockTopUpMobileFacade);
    }

    @Test
    public void verifyAndCreateTopUpMobileDraft(){

        Mockito.when(mockTopUpMobileFacade.verifyTopUpMobile(Mockito.any(VerifyTopUpAirtimeRequest.class))).thenReturn(createTopUpMobile());

        TopUpMobileDraft topUpMobileDraft = topUpMobileServiceImpl.verifyAndCreateTopUpMobileDraft("0868185055", new BigDecimal(0) , "12345");
        assertEquals("130419013811", topUpMobileDraft.getTransactionID());

        TopUpMobileDraft topUpMobileDraftFromRepo = transactionRepo.findDraftTransaction(topUpMobileDraft.getID(), "12345", TopUpMobileDraft.class);
        assertNotNull(topUpMobileDraftFromRepo);
        assertEquals("130419013811", topUpMobileDraftFromRepo.getTransactionID());
    }

    @Test
    public void sendOTP(){

        OTP otp = new OTP();
        otp.setMobileNumber("0868185055");
        otp.setOtpString("020406");
        otp.setReferenceCode("rdmf");

        Mockito.when(otpService.send(any(String.class))).thenReturn(otp);

        OTP otpResponse = topUpMobileServiceImpl.requestOTP(topUpMobileDraft.getID(), accessToken.getAccessTokenID());
        assertEquals(otpResponse, otp);

        TopUpMobileDraft topUpMobileDraftResponse = transactionRepo.findDraftTransaction(topUpMobileDraft.getID(), accessToken.getAccessTokenID(), TopUpMobileDraft.class);
        assertNotNull(topUpMobileDraftResponse);
        assertEquals(topUpMobileDraftResponse.getOtpReferenceCode(), otp.getReferenceCode());
        assertEquals(topUpMobileDraftResponse.getStatus(), TopUpMobileDraft.Status.OTP_SENT);
    }

    private TopUpMobile createTopUpMobile() {
        TopUpMobile topUpMobile = new TopUpMobile();
        topUpMobile.setID("130419013811");

        return topUpMobile;
    }

}
