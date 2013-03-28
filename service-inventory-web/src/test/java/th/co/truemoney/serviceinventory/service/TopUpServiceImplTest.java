package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuoteStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.TopUpStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TopUpServiceImplTest {

	private AsyncService asyncService;
	private TopUpServiceImpl topUpService;
	private EwalletSoapProxy ewalletSoapProxyMock;
	private AccessTokenRepository accessTokenRepoMock;
	private SourceOfFundRepository sourceOfFundRepoMock;
	private OTPService otpService;

	private DirectDebit directDebitDetail;

	@Before
	public void setup() {
		this.topUpService = new TopUpServiceImpl();
		this.ewalletSoapProxyMock = Mockito.mock(EwalletSoapProxy.class);
		this.accessTokenRepoMock = Mockito.mock(AccessTokenRepository.class);
		this.sourceOfFundRepoMock = Mockito.mock(SourceOfFundRepository.class);
		this.otpService = mock(OTPService.class);

		this.topUpService.setEWalletProxy(this.ewalletSoapProxyMock);
		this.topUpService.setAccessTokenRepository(this.accessTokenRepoMock);
		this.topUpService.setSourceOfFundRepository(this.sourceOfFundRepoMock);
		this.topUpService.setDirectDebitConfig(new DirectDebitConfigImpl());
		this.topUpService.setOrderRepository(new TransactionMemoryRepository());
		this.topUpService.setOtpService(otpService);


		directDebitDetail = new DirectDebit();
		directDebitDetail.setBankAccountNumber("xxxx5555");
		directDebitDetail.setBankCode("SCB");
		directDebitDetail.setBankNameEn("Siam Comercial Bank");
		directDebitDetail.setMinAmount(new BigDecimal(300));
		directDebitDetail.setMaxAmount(new BigDecimal(30000));
		directDebitDetail.setSourceOfFundID("111111111");
	}

	@Test
	public void createTopUpQuoteFromDirectDebitSuccess() {

		String sourceOfFundID = "11111111";
		String accessTokenID = "1234567890";
		BigDecimal amount = new BigDecimal(400);

		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.any(AccessToken.class)))
			.thenReturn(directDebitDetail);

		//when
		TopUpQuote topupQuote = this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);

		//then
		assertNotNull(topupQuote);
	}

	@Test
	public void createTopUpQuoteFromDirectDebitFailLessThanMinAmount() {

		String sourceOfFundID = "11111111";
		String accessTokenID = "1234567890";
		BigDecimal amount = new BigDecimal(30);

		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.any(AccessToken.class)))
			.thenReturn(directDebitDetail);

		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			//then
			assertEquals("20001", e.getCode());
		}
	}

	@Test
	public void createTopUpQuoteFromDirectDebitFailMostThanMaxAmount() {

		String sourceOfFundID = "11111111";
		String accessTokenID = "1234567890";
		BigDecimal amount = new BigDecimal(50000);

		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.any(AccessToken.class)))
			.thenReturn(directDebitDetail);

		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			//then
			assertEquals("20002", e.getCode());
		}
	}

	@Test
	public void confirmPlaceOrder() {
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessTokenID("1");
		accessToken.setMobileNumber("0890123456");
		accessToken.setSessionID("sessionID");
		accessToken.setTruemoneyID("truemoneyID");
		accessToken.setChannelID(41);

		TopUpQuote quote = new TopUpQuote();
		quote.setID("1");
		quote.setAccessTokenID("1");

		OTP otp = new OTP();
		otp.setOtpString("otpString");

		DirectDebit debit = new DirectDebit();
		debit.setSourceOfFundID("1");

		TopUpConfirmationInfo topUpConfirmationInfo = new TopUpConfirmationInfo();
		topUpConfirmationInfo.setTransactionID("1234");
		topUpConfirmationInfo.setTransactionDate("dd/mm/yyyy");

		quote.setSourceOfFund(debit);

		asyncService = mock(AsyncService.class);
		TransactionRepository orderRepo = mock(TransactionRepository.class);

		when(orderRepo.getTopUpEwalletDraftTransaction(anyString())).thenReturn(quote);
		when(accessTokenRepoMock.getAccessToken(anyString())).thenReturn(accessToken);
		when(otpService.isValidOTP(any(OTP.class))).thenReturn(true);

		topUpService.setAsyncService(asyncService);
		topUpService.setOrderRepository(orderRepo);
		topUpService.setOtpService(otpService);

		TopUpQuoteStatus quoteStatus = topUpService.confirmOTP(quote.getID(), otp, "accessToken");

		assertEquals(TopUpQuoteStatus.OTP_CONFIRMED, quoteStatus);
		verify(asyncService).topUpUtibaEwallet(any(TopUpOrder.class), any(AddMoneyRequest.class));
	}

	@Test
	public void confirmPlaceOrderFailAccessTokenNotFound() {
		TransactionRepository orderRepo = mock(TransactionRepository.class);
		when(orderRepo.getTopUpEwalletTransaction(anyString())).thenReturn(new TopUpOrder());
		when(accessTokenRepoMock.getAccessToken(anyString())).thenThrow(new ServiceInventoryException(ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
				"access token not found."));
		topUpService.setOrderRepository(orderRepo);

		try {
			topUpService.confirmOTP("1", new OTP(), "accessToken");
		} catch (ServiceInventoryException e) {
			assertEquals("10001", e.getCode());
		}
	}

	@Test
	public void confirmPlaceOrderFailOTPStringNotFound() {

		TransactionRepository orderRepo = mock(TransactionRepository.class);
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessTokenID("1");
		when(accessTokenRepoMock.getAccessToken(anyString())).thenReturn(accessToken);
		when(orderRepo.getTopUpEwalletDraftTransaction(anyString())).thenReturn(new TopUpQuote("1", directDebitDetail, "1", "username", new BigDecimal(300), new BigDecimal(20.0)));
		when(otpService.isValidOTP(any(OTP.class))).thenThrow(new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_FOUND,"otp not found."));

		topUpService.setOrderRepository(orderRepo);

		try {
			topUpService.confirmOTP("1", new OTP(), "accessToken");
		} catch (ServiceInventoryException e) {
			assertEquals("1003", e.getCode());
		}
	}

	@Test
	public void confirmPlaceOrderFailOTPNotMatch() {
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessTokenID("1");
		accessToken.setMobileNumber("0890123456");
		TopUpQuote quote = new TopUpQuote();
		quote.setSourceOfFund(directDebitDetail);
		quote.setAccessTokenID("1");
		quote.setID("1");
		OTP otp = new OTP("0890123456", "refCode", "otpString");

		asyncService = mock(AsyncService.class);
		TransactionRepository orderRepo = mock(TransactionRepository.class);

		when(orderRepo.getTopUpEwalletDraftTransaction(anyString())).thenReturn(quote);
		when(accessTokenRepoMock.getAccessToken(anyString())).thenReturn(accessToken);
		when(otpService.isValidOTP(any(OTP.class))).thenReturn(true);

		topUpService.setAsyncService(asyncService);
		topUpService.setOrderRepository(orderRepo);
		topUpService.setOtpService(otpService);

		try {
			topUpService.confirmOTP(quote.getID(), otp, "accessToken");
		} catch (ServiceInventoryException e) {
			assertEquals("1001", e.getCode());
		}
	}
}