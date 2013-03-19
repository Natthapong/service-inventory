package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.TopUpStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TopUpServiceImplTest {

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

}
