package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, LocalEnvironmentConfig.class, MemRepositoriesConfig.class })
@ActiveProfiles(profiles = {"local", "mem"})
public class TopUpServiceImplConfirmOTPTest {

	@Autowired
	private TopUpServiceImpl topUpService;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private OTPRepository otpRepo;

	private AsyncTopUpEwalletProcessor asyncServiceMock;

	private AccessToken accessToken;
	private TopUpQuote quote;


	@Before
	public void setup() {

		asyncServiceMock = mock(AsyncTopUpEwalletProcessor.class);
		this.topUpService.setAsyncTopUpProcessor(asyncServiceMock);

		//given
		accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();

		quote = new TopUpQuote();
		quote.setID("quoteID");
		quote.setStatus(Status.OTP_SENT);
		quote.setAccessTokenID(accessToken.getAccessTokenID());

		accessTokenRepo.save(accessToken);
		transactionRepo.saveTopUpEwalletDraftTransaction(quote, accessToken.getAccessTokenID());

		OTP goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");
		otpRepo.saveOTP(goodOTP);
	}


	@Test
	public void shouldConfirmOTPSuccess() {

		OTP goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");

		DraftTransaction.Status quoteStatus = topUpService.confirmOTP(quote.getID(), goodOTP, accessToken.getAccessTokenID());

		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, quoteStatus);
		verify(asyncServiceMock).topUpUtibaEwallet(any(TopUpOrder.class), any(AccessToken.class));
	}

	@Test
	public void shouldFailWhenConfirmWithBadAccessToken() {

		OTP invalidOTP = new OTP(accessToken.getMobileNumber(), "refCode", "HACKY");

		try {
			topUpService.confirmOTP(quote.getID(), invalidOTP, "unknown access token");
		} catch (ServiceInventoryException e) {
			assertEquals("10001", e.getCode());
		}

		//should never call the processor
		verify(asyncServiceMock, Mockito.never()).topUpUtibaEwallet(any(TopUpOrder.class), any(AccessToken.class));
	}

	@Test
	public void shouldFailWhenOTPStringIsIncorrect() {

		//when
		Assert.assertEquals(quote.getStatus(), DraftTransaction.Status.OTP_SENT);
		try {
			topUpService.confirmOTP(quote.getID(), new OTP(), accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ServiceInventoryException e) {}

		//then
		Assert.assertEquals(quote.getStatus(), DraftTransaction.Status.OTP_SENT);

		try {
			transactionRepo.getTopUpEwalletTransaction(quote.getID(), accessToken.getAccessTokenID());
			Assert.fail("should not create/persist any top up order");
		} catch(ServiceInventoryException e) {}

		//should never call the processor
		verify(asyncServiceMock, Mockito.never()).topUpUtibaEwallet(any(TopUpOrder.class), any(AccessToken.class));
	}
}
