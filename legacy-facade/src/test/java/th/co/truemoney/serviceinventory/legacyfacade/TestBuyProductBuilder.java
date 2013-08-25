package th.co.truemoney.serviceinventory.legacyfacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductConfirmationInfo;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BuyProxy;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler.ConfirmBuyProductFailException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler.VerifyBuyProductFailException;

public class TestBuyProductBuilder {

	private LegacyFacade legacyFacade;
	private BuyProductHandler buyProductFacade;
	private AccessToken accessToken;
	private ClientCredential appData;
	private BuyProxy buyProxyMock;
	
	@Before
	public void before() {
        this.accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
		this.appData = new ClientCredential("appKey", "appUser", "appPassword", "channel", "channelDetail");
		this.buyProxyMock = mock(BuyProxy.class);
		
		this.legacyFacade = new LegacyFacade();
		this.buyProductFacade = new BuyProductHandler();
		this.legacyFacade.setBuyProductFacade(buyProductFacade);
		this.buyProductFacade.setBuyProxy(this.buyProxyMock);
	}
	
	@After
	public void after() {
		reset(buyProxyMock);
	}
	
	@Test
	public void verifyBuyProductSuccess() {
		//given 
		when(buyProxyMock.verifyBuyProduct(any(VerifyBuyRequest.class))).thenReturn(createStubbedVerifyBuyProductResponse());
		
		//when
		BuyProduct buyProduct = legacyFacade.buyProduct()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.withTargetProduct("ecash_c")
				.toRecipientMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")	
				.withAmount(BigDecimal.TEN)
				.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
				.verifyBuyProduct();
		
		//then 
		assertNotNull(buyProduct);
		assertEquals("verifyID", buyProduct.getID());
		verify(buyProxyMock).verifyBuyProduct(any(VerifyBuyRequest.class));
	}

	@Test(expected=VerifyBuyProductFailException.class)
	public void verifyBuyProductFailWithResultCode() {
		//given 
		when(buyProxyMock.verifyBuyProduct(any(VerifyBuyRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","namespace"));
		
		//when
		legacyFacade.buyProduct()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.withTargetProduct("ecash_c")
				.toRecipientMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")	
				.withAmount(BigDecimal.TEN)
				.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
				.verifyBuyProduct();
		
		//then 
		verify(buyProxyMock).verifyBuyProduct(any(VerifyBuyRequest.class));
	}
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void verifyBuyProductFailWithUnExpectedCode() {
		//given 
		when(buyProxyMock.verifyBuyProduct(any(VerifyBuyRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		legacyFacade.buyProduct()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.withTargetProduct("ecash_c")
				.toRecipientMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")	
				.withAmount(BigDecimal.TEN)
				.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
				.verifyBuyProduct();
		
		//then 
		verify(buyProxyMock).verifyBuyProduct(any(VerifyBuyRequest.class));
	}
	
	@Test
	public void confirmBuyProductSuccess() {
		//given 
		when(buyProxyMock.confirmBuyProduct(any(ConfirmBuyRequest.class))).thenReturn(createStubbedConfirmBuyProductResponse());
		
		//when
		BuyProductConfirmationInfo confirmInfo = legacyFacade.buyProduct()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.withTargetProduct("ecash_c")
				.toRecipientMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")	
				.withAmount(BigDecimal.TEN)
				.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
				.confirmBuyProduct("transactionID");
		
		//then 
		assertNotNull(confirmInfo);
		assertEquals("ApproveID", confirmInfo.getTransactionID());
		verify(buyProxyMock).confirmBuyProduct(any(ConfirmBuyRequest.class));
	}
	
	@Test(expected=ConfirmBuyProductFailException.class)
	public void confirmBuyProductFailWithResultCode() {
		//given 
		when(buyProxyMock.confirmBuyProduct(any(ConfirmBuyRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","namespace"));
		
		//when
		legacyFacade.buyProduct()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.withTargetProduct("ecash_c")
			.toRecipientMobileNumber("08xxxxxxxx")
			.usingSourceOfFund("EW")	
			.withAmount(BigDecimal.TEN)
			.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
			.confirmBuyProduct("transactionID");
		
		//then 
		verify(buyProxyMock).confirmBuyProduct(any(ConfirmBuyRequest.class));
	}
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void confirmBuyProductFailWithUnExpectedCode() {
		//given 
		when(buyProxyMock.confirmBuyProduct(any(ConfirmBuyRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		legacyFacade.buyProduct()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.withTargetProduct("ecash_c")
			.toRecipientMobileNumber("08xxxxxxxx")
			.usingSourceOfFund("EW")	
			.withAmount(BigDecimal.TEN)
			.andFee(BigDecimal.ZERO, BigDecimal.ZERO)			
			.confirmBuyProduct("transactionID");
		
		//then 
		verify(buyProxyMock).confirmBuyProduct(any(ConfirmBuyRequest.class));
	}
	
	private VerifyBuyResponse createStubbedVerifyBuyProductResponse() {
		VerifyBuyResponse verifyBuyResponse = new VerifyBuyResponse(new SIEngineResponse());
		verifyBuyResponse.setTransactionID("verifyID");
		verifyBuyResponse.setResultCode("0");
		verifyBuyResponse.setResultNamespace("SIENGINE");
		return verifyBuyResponse;
	}
	
	private ConfirmBuyResponse createStubbedConfirmBuyProductResponse() {
		SIEngineResponse siengineResponse = new SIEngineResponse();
		siengineResponse.addParameterElement("approve_code", "ApproveID");
		ConfirmBuyResponse confirmBuyResponse = new ConfirmBuyResponse(siengineResponse);
		confirmBuyResponse.setTransactionID("transactionID");
		confirmBuyResponse.setResultCode("0");
		confirmBuyResponse.setResultNamespace("SIENGINE");
		return confirmBuyResponse;
	}
	
}
