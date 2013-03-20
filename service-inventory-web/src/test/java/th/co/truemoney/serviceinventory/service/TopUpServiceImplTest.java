package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyString;

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
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.TopUpStubbed;
import th.co.truemoney.serviceinventory.util.EncryptUtil;

@RunWith(MockitoJUnitRunner.class)
public class TopUpServiceImplTest {
	
	private AsyncService asyncService;
	private TopUpServiceImpl topUpService;
	private EwalletSoapProxy ewalletSoapProxyMock;
	private AccessTokenRepository accessTokenRepoMock;
	private SourceOfFundRepository sourceOfFundRepoMock;
	
	private DirectDebit directDebitDetail;
	
	@Before
	public void setup() {
		this.topUpService = new TopUpServiceImpl();
		this.ewalletSoapProxyMock = Mockito.mock(EwalletSoapProxy.class);
		this.accessTokenRepoMock = Mockito.mock(AccessTokenRepository.class);
		this.sourceOfFundRepoMock = Mockito.mock(SourceOfFundRepository.class);

		this.topUpService.setEWalletProxy(this.ewalletSoapProxyMock);
		this.topUpService.setAccessTokenRepository(this.accessTokenRepoMock);
		this.topUpService.setSourceOfFundRepository(this.sourceOfFundRepoMock);
		this.topUpService.setDirectDebitConfig(new DirectDebitConfigImpl());
		this.topUpService.setOrderRepository(new OrderMemoryRepository());
		
		directDebitDetail = new DirectDebit();
		directDebitDetail.setBankAccountNumber("xxxx5555");
		directDebitDetail.setBankCode("SCB");
		directDebitDetail.setBankNameEn("Siam Comercial Bank");
		directDebitDetail.setBankNameTh("ไทยพาณิชย์");
		directDebitDetail.setMinAmount(new BigDecimal(300));
		directDebitDetail.setMaxAmount(new BigDecimal(30000));
		directDebitDetail.setSourceOfFundID("111111111");
		directDebitDetail.setSourceOfFundType("DD");
	}
	
	@Test
	public void createTopUpQuoteFromDirectDebitSuccess() {
		
		String sourceOfFundID = "11111111";
		String accessTokenID = "1234567890";
		QuoteRequest quoteRequest = new QuoteRequest();
		quoteRequest.setAmount(new BigDecimal(400));
		quoteRequest.setChecksum("xxxxxxxxxx");
		
		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()))
			.thenReturn(directDebitDetail);
		
		//when
		TopUpQuote topupQuote = this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);

		//then
		assertNotNull(topupQuote);
	}
	
	@Test
	public void createTopUpQuoteFromDirectDebitFailLessThanMinAmount() {
		
		String sourceOfFundID = "11111111";
		String accessTokenID = "1234567890";
		QuoteRequest quoteRequest = new QuoteRequest();
		quoteRequest.setAmount(new BigDecimal(30));
		quoteRequest.setChecksum("xxxxxxxxxx");
		
		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()))
			.thenReturn(directDebitDetail);
		
		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);
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
		QuoteRequest quoteRequest = new QuoteRequest();
		quoteRequest.setAmount(new BigDecimal(50000));
		quoteRequest.setChecksum("xxxxxxxxxx");
		
		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = TopUpStubbed.createSuccessStubbedStandardMoneyResponse();

		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
		when(sourceOfFundRepoMock.getUserDirectDebitSourceByID(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()))
			.thenReturn(directDebitDetail);
		
		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			//then
			assertEquals("20002", e.getCode());
		}
	}

	@Test 
	public void confirmPlaceOrder() {
		AccessToken accessToken = new AccessToken();
		TopUpOrder topUpOrder = new TopUpOrder();
		topUpOrder.setID("1");
		OTP otp = new OTP();
		String localChecksum = EncryptUtil.buildHmacSignature("accessToken", topUpOrder.toString()+"accessToken");
		otp.setChecksum(localChecksum);
		
		asyncService = mock(AsyncService.class);
		AccessTokenRepository accessTokenRepo = mock(AccessTokenRepository.class);
		OrderRepository orderRepo = mock(OrderRepository.class);
		
		when(orderRepo.getTopUpOrder(anyString())).thenReturn(topUpOrder);
		when(accessTokenRepo.getAccessToken(anyString())).thenReturn(accessToken);
		
		topUpService.setAsyncService(asyncService);
		topUpService.setOrderRepo(orderRepo);
		topUpService.setAccessTokenRepo(accessTokenRepo);
		
				
		TopUpOrder order = topUpService.confirmPlaceOrder(topUpOrder.getID(), otp, "accessToken");
		
		assertEquals(TopUpStatus.PROCESSING, order.getStatus());
		verify(asyncService).topUpUtibaEwallet(topUpOrder, accessToken);
		verify(orderRepo).saveTopUpOrder(topUpOrder);
	}
}