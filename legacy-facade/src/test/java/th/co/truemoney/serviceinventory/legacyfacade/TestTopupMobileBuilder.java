package th.co.truemoney.serviceinventory.legacyfacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler.ConfirmTopupAirtimeFailException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler.VerifyTopUpAirtimeFailException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;

public class TestTopupMobileBuilder {

	private LegacyFacade legacyFacade;
	private MobileTopUpHandler mobileTopupFacade;
	private AccessToken accessToken;
	private ClientCredential appData;
	private TopUpMobileProxy topupMobileProxyMock;
	
	@Before
	public void before() {
        this.accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
		this.appData = new ClientCredential("appKey", "appUser", "appPassword", "channel", "channelDetail");
		this.topupMobileProxyMock = mock(TopUpMobileProxy.class);
		
		this.legacyFacade = new LegacyFacade();
		this.mobileTopupFacade = new MobileTopUpHandler();
		this.mobileTopupFacade.setTopupMobileProxy(topupMobileProxyMock);
		this.legacyFacade.setTopUpMobileFacade(mobileTopupFacade);
	}
	
	@After
	public void after() {
		reset(topupMobileProxyMock);
	}
	
	@Test
	public void verifyTopupMobileSuccessWithFixServiceFee() {
		//given
		when(topupMobileProxyMock.verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class)))
			.thenReturn(createStubbedVerifyResponseFixServiceFee());
		
		//when
		TopUpMobile topUpMobile = legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")
				.withAmount(BigDecimal.TEN)
				.verifyTopUpAirtime();
		
		//then
		assertNotNull(topUpMobile);
		assertEquals("verifyID", topUpMobile.getID());
		verify(topupMobileProxyMock).verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class));
	} 
	
	@Test
	public void verifyTopupMobileSuccessWithPercentServiceFee() {
		//given
		when(topupMobileProxyMock.verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class)))
			.thenReturn(createStubbedVerifyResponsePercentServiceFee());
		
		//when
		TopUpMobile topUpMobile = legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")
				.withAmount(BigDecimal.TEN)
				.verifyTopUpAirtime();
		
		//then
		assertNotNull(topUpMobile);
		assertEquals("verifyID", topUpMobile.getID());
		verify(topupMobileProxyMock).verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class));
	} 
	
	@Test(expected=VerifyTopUpAirtimeFailException.class)
	public void verifyTopupMobileFailWithResultCode() {
		//given
		when(topupMobileProxyMock.verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		legacyFacade.topUpMobile()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toMobileNumber("08xxxxxxxx")
			.usingSourceOfFund("EW")
			.withAmount(BigDecimal.TEN)
			.verifyTopUpAirtime();
		
		//then
		verify(topupMobileProxyMock).verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class));
	} 
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void verifyTopupMobileFailWithUnExpectedCode() {
		//given
		when(topupMobileProxyMock.verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		legacyFacade.topUpMobile()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toMobileNumber("08xxxxxxxx")
			.usingSourceOfFund("EW")
			.withAmount(BigDecimal.TEN)
			.verifyTopUpAirtime();
		
		//then
		verify(topupMobileProxyMock).verifyTopUpAirtime(any(VerifyTopUpAirtimeRequest.class));
	} 
	
	@Test
	public void topupMobileSuccess() {
		//given
		when(topupMobileProxyMock.confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class)))
			.thenReturn(createStubbedConfirmResponse());
		
		//when
		TopUpMobile topUpMobile = createTopupMobile();		
		TopUpMobileConfirmationInfo confirmationInfo = legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")
				.withAmount(BigDecimal.TEN)
				.andFee(topUpMobile.getServiceFee().calculateFee(BigDecimal.TEN), topUpMobile.getEwalletSourceOfFund().calculateFee(BigDecimal.TEN))
				.topUpAirtime("VerifyID", "tmvh");
		
		//then
		assertNotNull(confirmationInfo);
		assertEquals("ApproveID", confirmationInfo.getTransactionID());
		verify(topupMobileProxyMock).confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class));
	}
	
	@Test(expected=ConfirmTopupAirtimeFailException.class)
	public void topupMobileFailWithResultCode() {
		//given
		when(topupMobileProxyMock.confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		TopUpMobile topUpMobile = createTopupMobile();		
		legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")
				.withAmount(BigDecimal.TEN)
				.andFee(topUpMobile.getServiceFee().calculateFee(BigDecimal.TEN), topUpMobile.getEwalletSourceOfFund().calculateFee(BigDecimal.TEN))
				.topUpAirtime("VerifyID", "tmvh");
		
		//then
		verify(topupMobileProxyMock).confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class));
	}
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void topupMobileFailWithUnExpectedCode() {
		//given
		when(topupMobileProxyMock.confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		TopUpMobile topUpMobile = createTopupMobile();		
		legacyFacade.topUpMobile()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toMobileNumber("08xxxxxxxx")
				.usingSourceOfFund("EW")
				.withAmount(BigDecimal.TEN)
				.andFee(topUpMobile.getServiceFee().calculateFee(BigDecimal.TEN), topUpMobile.getEwalletSourceOfFund().calculateFee(BigDecimal.TEN))
				.topUpAirtime("VerifyID", "tmvh");
		
		//then
		verify(topupMobileProxyMock).confirmTopUpAirtime(any(ConfirmTopUpAirtimeRequest.class));
	}

	private TopUpMobile createTopupMobile() {
		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setServiceFee(new ServiceFeeInfo("THB",new BigDecimal("10")));
		
		SourceOfFund sourceOfFundEwallet = new SourceOfFund();
		sourceOfFundEwallet.setSourceType("EW");
		sourceOfFundEwallet.setFeeRateType("THB");
		sourceOfFundEwallet.setFeeRate(BigDecimal.TEN);
		sourceOfFundEwallet.setMinFeeAmount(BigDecimal.ONE);
		sourceOfFundEwallet.setMaxFeeAmount(new BigDecimal("20"));		
		
		SourceOfFund sourceOfFundCreditCard = new SourceOfFund();
		sourceOfFundCreditCard.setSourceType("CreditCard");
		sourceOfFundCreditCard.setFeeRateType("Percent");
		sourceOfFundCreditCard.setFeeRate(new BigDecimal("3"));
		sourceOfFundCreditCard.setMinFeeAmount(BigDecimal.ONE);
		sourceOfFundCreditCard.setMaxFeeAmount(new BigDecimal("20"));		
		
		SourceOfFund[] sourceOfFunds = new SourceOfFund[2];
		sourceOfFunds[0] = sourceOfFundEwallet;
		sourceOfFunds[1] = sourceOfFundCreditCard;
		topUpMobile.setSourceOfFundFees(sourceOfFunds);
		
		return topUpMobile;
	} 
	
	private VerifyTopUpAirtimeResponse createStubbedVerifyResponseFixServiceFee() {
		SIEngineResponse siengineResponse = new SIEngineResponse();

		SourceFee sourceFeeEwallet = new SourceFee();
		sourceFeeEwallet.setSource("EW");
		sourceFeeEwallet.setSourceFeeType("THB");
		sourceFeeEwallet.setSourceFee("10000");
		sourceFeeEwallet.setTotalSourceFee("10000");
		sourceFeeEwallet.setMaxAmount("20000");
		sourceFeeEwallet.setMinAmount("100");
		
		SourceFee sourceFeeCreditCard = new SourceFee();
		sourceFeeCreditCard.setSource("CreditCard");
		sourceFeeCreditCard.setSourceFeeType("Percent");
		sourceFeeCreditCard.setSourceFee("30");
		sourceFeeCreditCard.setTotalSourceFee("30");
		sourceFeeCreditCard.setMaxAmount("20000");
		sourceFeeCreditCard.setMinAmount("100");
		
		List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();
		sourceFeeList.add(sourceFeeEwallet);
		sourceFeeList.add(sourceFeeCreditCard);
		siengineResponse.getExtraXML().setSourceFeeList(sourceFeeList);		
		
		siengineResponse.addParameterElement("service_fee_type", "THB");
		siengineResponse.addParameterElement("service_fee", "10000");
		siengineResponse.addParameterElement("total_service_fee", "10000");
		siengineResponse.addParameterElement("service_max_amount", "20000");
		siengineResponse.addParameterElement("service_min_amount", "100");

		VerifyTopUpAirtimeResponse verifyResponse = new VerifyTopUpAirtimeResponse(siengineResponse);
		verifyResponse.setTransactionID("verifyID");
		verifyResponse.setResultCode("0");
		verifyResponse.setResultNamespace("SIENGINE");
		return verifyResponse;
	}
	
	private VerifyTopUpAirtimeResponse createStubbedVerifyResponsePercentServiceFee() {
		SIEngineResponse siengineResponse = new SIEngineResponse();

		SourceFee sourceFeeEwallet = new SourceFee();
		sourceFeeEwallet.setSource("EW");
		sourceFeeEwallet.setSourceFeeType("THB");
		sourceFeeEwallet.setSourceFee("10000");
		sourceFeeEwallet.setTotalSourceFee("10000");
		sourceFeeEwallet.setMaxAmount("20000");
		sourceFeeEwallet.setMinAmount("100");
		
		SourceFee sourceFeeCreditCard = new SourceFee();
		sourceFeeCreditCard.setSource("CreditCard");
		sourceFeeCreditCard.setSourceFeeType("Percent");
		sourceFeeCreditCard.setSourceFee("30");
		sourceFeeCreditCard.setTotalSourceFee("30");
		sourceFeeCreditCard.setMaxAmount("20000");
		sourceFeeCreditCard.setMinAmount("100");
		
		List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();
		sourceFeeList.add(sourceFeeEwallet);
		sourceFeeList.add(sourceFeeCreditCard);
		siengineResponse.getExtraXML().setSourceFeeList(sourceFeeList);		
		
		siengineResponse.addParameterElement("service_fee_type", "Percent");
		siengineResponse.addParameterElement("service_fee", "3");
		siengineResponse.addParameterElement("total_service_fee", "30");
		siengineResponse.addParameterElement("service_max_amount", "20000");
		siengineResponse.addParameterElement("service_min_amount", "000");

		VerifyTopUpAirtimeResponse verifyResponse = new VerifyTopUpAirtimeResponse(siengineResponse);
		verifyResponse.setTransactionID("verifyID");
		verifyResponse.setResultCode("0");
		verifyResponse.setResultNamespace("SIENGINE");
		return verifyResponse;
	}
	
	private ConfirmTopUpAirtimeResponse createStubbedConfirmResponse() {
		SIEngineResponse siengineResponse = new SIEngineResponse();
		siengineResponse.addParameterElement("approve_code", "ApproveID");
		ConfirmTopUpAirtimeResponse confirmResponse = new ConfirmTopUpAirtimeResponse(siengineResponse);
		confirmResponse.setTransactionID("transactionID");
		confirmResponse.setResultCode("0");
		confirmResponse.setResultNamespace("SIENGINE");
		return confirmResponse;
	}
}
