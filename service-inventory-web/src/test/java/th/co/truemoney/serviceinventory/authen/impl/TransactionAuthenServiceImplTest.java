package th.co.truemoney.serviceinventory.authen.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.OTPAlreadyConfirmedException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, LocalEnvironmentConfig.class, MemRepositoriesConfig.class })
@ActiveProfiles(profiles = {"local", "mem"})
@Category(IntegrationTest.class)
public class TransactionAuthenServiceImplTest {

    @Autowired
    private TransactionAuthenServiceImpl authenService;

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private OTPRepository otpRepo;

    private OTPService otpServiceMock;
    private AsyncTopUpEwalletProcessor asyncServiceMock;

    private AccessToken accessToken;

    private OTP goodOTP;

    private TopUpQuote quote;

    @Before
    public void setup() {

        otpServiceMock = Mockito.mock(OTPService.class);
        asyncServiceMock = mock(AsyncTopUpEwalletProcessor.class);

        //given
        accessToken =  new AccessToken("tokenID", "loginID", "sessionID", "tmnID", 41);
        accessTokenRepo.save(accessToken);

        goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");
        otpRepo.save(goodOTP);

        quote =  createQuote(accessToken, goodOTP);
        transactionRepo.saveDraftTransaction(quote, accessToken.getAccessTokenID());
    }

    @Test
    public void shouldConfirmOTPSuccess() {

        OTP goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");

        TopUpQuote.Status quoteStatus = authenService.verifyOTP(quote.getID(), goodOTP, accessToken.getAccessTokenID());

        assertEquals(TopUpQuote.Status.OTP_CONFIRMED, quoteStatus);
    }

    @Test
    public void shouldFailWhenConfirmWithBadAccessToken() {

        OTP invalidOTP = new OTP(accessToken.getMobileNumber(), "refCode", "HACKY");

        try {
            authenService.verifyOTP(quote.getID(), invalidOTP, "unknown access token");
        } catch (ServiceInventoryWebException e) {
            assertEquals("10001", e.getErrorCode());
        }

        //should never call the processor
        verify(asyncServiceMock, Mockito.never()).topUpUtibaEwallet(any(TopUpOrder.class), any(AccessToken.class));
    }

    @Test
    public void shouldFailWhenConfirmOTPStringIsIncorrect() {

        //when
        Assert.assertEquals(TopUpQuote.Status.OTP_SENT, quote.getStatus());
        Mockito.doThrow(new ServiceInventoryWebException("error", "otp error")).when(otpServiceMock).isValidOTP(any(OTP.class));
        try {
            authenService.verifyOTP(quote.getID(), new OTP(), accessToken.getAccessTokenID());
            Assert.fail();
        } catch (ServiceInventoryWebException e) {}

        //then
        Assert.assertEquals(TopUpQuote.Status.OTP_SENT, quote.getStatus());

        try {
            transactionRepo.findTransaction(quote.getID(), accessToken.getAccessTokenID(), TopUpOrder.class);
            Assert.fail("should not create/persist any top up order");
        } catch(ServiceInventoryWebException e) {}

        //should never call the processor
        verify(asyncServiceMock, Mockito.never()).topUpUtibaEwallet(any(TopUpOrder.class), any(AccessToken.class));
    }

    @Test
    public void shouldFailWhenRequestingOTPOnConfirmedDraft() {

    	quote.setStatus(Status.OTP_CONFIRMED);
        transactionRepo.saveDraftTransaction(quote, accessToken.getAccessTokenID());

    	Assert.assertEquals(TopUpQuote.Status.OTP_CONFIRMED, quote.getStatus());

    	try {
            authenService.requestOTP(quote.getID(), accessToken.getAccessTokenID());
            Assert.fail();
        } catch (OTPAlreadyConfirmedException e) {

        }

        //then
        Assert.assertEquals(TopUpQuote.Status.OTP_CONFIRMED, quote.getStatus());
    }

    @Test
    public void shouldFailWhenVerifyingOTPOnConfirmedDraft() {

    	quote.setStatus(Status.OTP_CONFIRMED);
        transactionRepo.saveDraftTransaction(quote, accessToken.getAccessTokenID());

    	Assert.assertEquals(TopUpQuote.Status.OTP_CONFIRMED, quote.getStatus());

    	try {
            authenService.verifyOTP(quote.getID(), new OTP(), accessToken.getAccessTokenID());
            Assert.fail();
        } catch (OTPAlreadyConfirmedException e) {

        }

        //then
        Assert.assertEquals(TopUpQuote.Status.OTP_CONFIRMED, quote.getStatus());
    }

    @Test
    public void shouldNotSendOTPWhenUseAppleUser() {
        
        //given
    	accessToken =  new AccessToken("tokenID", "loginID", "sessionID", "tmn.10000000020", 41);
    	accessToken.setMobileNumber("0866013468");
        accessTokenRepo.save(accessToken);
        
    	//when
        authenService.requestOTP(quote.getID(), accessToken.getAccessTokenID());
        
        //then
        verify(otpServiceMock, never()).send(anyString());

    }

    
    private TopUpQuote createQuote(AccessToken accessToken, OTP otp) {
        TopUpQuote quote = new TopUpQuote();
        quote.setID("quoteID");
        quote.setStatus(TopUpQuote.Status.OTP_SENT);
        quote.setOtpReferenceCode(otp.getReferenceCode());
        quote.setAccessTokenID(accessToken.getAccessTokenID());

        return quote;
    }

}
