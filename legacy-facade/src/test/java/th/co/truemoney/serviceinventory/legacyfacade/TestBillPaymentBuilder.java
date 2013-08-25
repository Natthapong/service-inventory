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

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler.ConfirmBillPayFailException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler.GetBillInformationFailException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler.VerifyBillPayFailException;

public class TestBillPaymentBuilder {

	private LegacyFacade legacyFacade;
	private BillPaymentHandler billPaymentFacade;
	private AccessToken accessToken;
	private ClientCredential appData;
	private BillProxy billProxyMock;
	
	@Before
	public void before() {
        this.accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
		this.appData = new ClientCredential("appKey", "appUser", "appPassword", "channel", "channelDetail");
		this.billProxyMock = mock(BillProxy.class);
		
		this.legacyFacade = new LegacyFacade();
		this.billPaymentFacade = new BillPaymentHandler();
		this.legacyFacade.setBillPaymentFacade(billPaymentFacade);
		this.billPaymentFacade.setBillPayProxy(this.billProxyMock);
	}
	
	@After
	public void after() {
		reset(billProxyMock);
	}
	
	@Test
	public void verifyBillPaymentSuccess() {
		//given
		when(billProxyMock.verifyBillPay(any(VerifyBillPayRequest.class)))
			.thenReturn(createStubbedVerifyResponse());
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
        String verificationID = legacyFacade.billing()
                .fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
                .aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
                .usingMobilePayPoint(accessToken.getMobileNumber())
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
                .verifyPayment();
		
		//then
		assertNotNull(verificationID);
		assertEquals("verifyID", verificationID);
		verify(billProxyMock).verifyBillPay(any(VerifyBillPayRequest.class));
	} 
	
	@Test(expected=VerifyBillPayFailException.class)
	public void verifyBillPaymentFailWithResultCode() {
		//given
		when(billProxyMock.verifyBillPay(any(VerifyBillPayRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
        legacyFacade.billing()
                .fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
                .aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
                .usingMobilePayPoint(accessToken.getMobileNumber())
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
                .verifyPayment();
		
		//then
		verify(billProxyMock).verifyBillPay(any(VerifyBillPayRequest.class));
	} 
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void verifyBillPaymentFailWithUnExpectedCode() {
		//given
		when(billProxyMock.verifyBillPay(any(VerifyBillPayRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
        legacyFacade.billing()
                .fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
                .aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
                .usingMobilePayPoint(accessToken.getMobileNumber())
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
                .verifyPayment();
		
		//then
		verify(billProxyMock).verifyBillPay(any(VerifyBillPayRequest.class));
	} 
	
	@Test
	public void confirmBillPaymentSuccess() {
		//given
		when(billProxyMock.confirmBillPay(any(ConfirmBillPayRequest.class)))
			.thenReturn(createStubbedConfirmResponse());
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
		BillPaymentConfirmationInfo confirmationInfo = legacyFacade.billing()
				.fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
				.aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingMobilePayPoint(accessToken.getMobileNumber())
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromBillChannel(appData.getChannel(), appData.getChannelDetail())
				.paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
				.performPayment("VerifyID");
		
		//then
		assertNotNull(confirmationInfo);
		assertEquals("ApproveID", confirmationInfo.getTransactionID());
		verify(billProxyMock).confirmBillPay(any(ConfirmBillPayRequest.class));
	}

	@Test(expected=ConfirmBillPayFailException.class)
	public void confirmBillPaymentFailWithResultCode() {
		//given
		when(billProxyMock.confirmBillPay(any(ConfirmBillPayRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
		legacyFacade.billing()
				.fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
				.aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingMobilePayPoint(accessToken.getMobileNumber())
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromBillChannel(appData.getChannel(), appData.getChannelDetail())
				.paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
				.performPayment("VerifyID");
		
		//then
		verify(billProxyMock).confirmBillPay(any(ConfirmBillPayRequest.class));
	}
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void confirmBillPaymentFailWithUnExpectedCode() {
		//given
		when(billProxyMock.confirmBillPay(any(ConfirmBillPayRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		BigDecimal amount = BigDecimal.TEN;
		Bill billInfo = createBillInfo(amount);
		legacyFacade.billing()
				.fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
				.aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingMobilePayPoint(accessToken.getMobileNumber())
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
				.fromBillChannel(appData.getChannel(), appData.getChannelDetail())
				.paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
				.performPayment("VerifyID");
		
		//then
		verify(billProxyMock).confirmBillPay(any(ConfirmBillPayRequest.class));
	}
	
	@Test
	public void getBarcodeInformationSuccessWithFixServiceFee() {
		//given
		when(billProxyMock.getBarcodeInformation(any(GetBarcodeRequest.class)))
			.thenReturn(createStubbedGetBarcodeResponse("THB"));
		
		//when
        Bill bill = legacyFacade.billing()
                .readBillInfoWithBarcode("|010554614953100 010004552 010520120200015601 85950")
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .read();
		
		//then
		assertNotNull(bill);
		assertEquals("tcg", bill.getTarget());
		verify(billProxyMock).getBarcodeInformation(any(GetBarcodeRequest.class));
	} 
	
	@Test
	public void getBarcodeInformationSuccessWithPercentService() {
		//given
		when(billProxyMock.getBarcodeInformation(any(GetBarcodeRequest.class)))
			.thenReturn(createStubbedGetBarcodeResponse("Percent"));
		
		//when
        Bill bill = legacyFacade.billing()
                .readBillInfoWithBarcode("|010554614953100 010004552 010520120200015601 85950")
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .read();
		
		//then
		assertNotNull(bill);
		assertEquals("tcg", bill.getTarget());
		verify(billProxyMock).getBarcodeInformation(any(GetBarcodeRequest.class));
	}

	@Test(expected=GetBillInformationFailException.class)
	public void getBarcodeInformationFailWithResultCode() {
		//given
		when(billProxyMock.getBarcodeInformation(any(GetBarcodeRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		legacyFacade.billing()
	        .readBillInfoWithBarcode("|010554614953100 010004552 010520120200015601 85950")
	        .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
	        .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
	        .read();
		
		//then
		verify(billProxyMock).getBarcodeInformation(any(GetBarcodeRequest.class));
	} 
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void getBarcodeInformationFailUnExpectedCode() {
		//given
		when(billProxyMock.getBarcodeInformation(any(GetBarcodeRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		legacyFacade.billing()
	        .readBillInfoWithBarcode("|010554614953100 010004552 010520120200015601 85950")
	        .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
	        .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
	        .read();
		
		//then
		verify(billProxyMock).getBarcodeInformation(any(GetBarcodeRequest.class));
	} 
	
	@Test
	public void getBillCodeInformationSuccess() {
		//given
		when(billProxyMock.getBillCodeInformation(any(GetBillRequest.class)))
			.thenReturn(createStubbedGetBillcodeResponse());
		
		//when
        Bill bill = legacyFacade.billing().readBillInfoWithBillCode("tcg")
                .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
                .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
                .read();
		
		//then
		assertNotNull(bill);
		assertEquals("tcg", bill.getTarget());
		verify(billProxyMock).getBillCodeInformation(any(GetBillRequest.class));
	} 
	
	@Test(expected=GetBillInformationFailException.class)
	public void getBillCodeInformationFailWithResultCode() {
		//given
		when(billProxyMock.getBillCodeInformation(any(GetBillRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
		legacyFacade.billing().readBillInfoWithBillCode("tcg")
	        .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
	        .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
	        .read();
		
		//then
		verify(billProxyMock).getBillCodeInformation(any(GetBillRequest.class));
	} 
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void getBillCodeInformationFailUnExpectedCode() {
		//given
		when(billProxyMock.getBillCodeInformation(any(GetBillRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
		legacyFacade.billing().readBillInfoWithBillCode("tcg")
	        .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
	        .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
	        .read();
		
		//then
		verify(billProxyMock).getBillCodeInformation(any(GetBillRequest.class));
	} 
	
	@Test
	public void getBillOutStandingOnlineSuccess() {
		//given
		when(billProxyMock.inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class)))
			.thenReturn(createStubbedInquiryOutstandingBillResponse());
		
		//when
        OutStandingBill outStandingBill =  legacyFacade.billing()
            .readBillOutStandingOnlineWithBillCode("tmvh")
            .fromRef1("08xxxxxxxx")
            .fromOperateByStaff("08xxxxxxxx")
            .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
            .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
            .getBillOutStandingOnline();
		
		//then
		assertNotNull(outStandingBill);
		assertEquals(new BigDecimal(100), outStandingBill.getOutStandingBalance());
		verify(billProxyMock).inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class));
	} 
	
	@Test(expected=GetBillInformationFailException.class)
	public void getBillOutStandingOnlineFailWithResultCode() {
		//given
		when(billProxyMock.inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class)))
			.thenThrow(new FailResultCodeException("19xxxx","SIENGINE"));
		
		//when
        legacyFacade.billing()
            .readBillOutStandingOnlineWithBillCode("tmvh")
            .fromRef1("08xxxxxxxx")
            .fromOperateByStaff("08xxxxxxxx")
            .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
            .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
            .getBillOutStandingOnline();
		
		//then
		verify(billProxyMock).inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class));
	} 
	
	@Test(expected=SIEngineUnExpectedException.class)
	public void getBillOutStandingOnlineFailUnExpectedCode() {
		//given
		when(billProxyMock.inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class)))
			.thenThrow(new RestClientException("Connection timed out"));
		
		//when
        legacyFacade.billing()
            .readBillOutStandingOnlineWithBillCode("tmvh")
            .fromRef1("08xxxxxxxx")
            .fromOperateByStaff("08xxxxxxxx")
            .fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
            .fromBillChannel(appData.getChannel(), appData.getChannelDetail())
            .getBillOutStandingOnline();
		
		//then
		verify(billProxyMock).inquiryOutstandingBill(any(InquiryOutstandingBillRequest.class));
	} 
	
	private InquiryOutstandingBillResponse createStubbedInquiryOutstandingBillResponse() {
		SIEngineResponse siengineResponse = createBillInfoResponse("THB");		
		InquiryOutstandingBillResponse billResponse = new InquiryOutstandingBillResponse(siengineResponse);
		billResponse.setTransactionID("GetBillInfoID");
		billResponse.setResultCode("0");
		billResponse.setResultNamespace("SIENGINE");
		return billResponse;
	}

	private GetBillResponse createStubbedGetBillcodeResponse() {
		SIEngineResponse siengineResponse = createBillInfoResponse("THB");		
		GetBillResponse billResponse = new GetBillResponse(siengineResponse);
		billResponse.setTransactionID("GetBillInfoID");
		billResponse.setResultCode("0");
		billResponse.setResultNamespace("SIENGINE");
		return billResponse;
	}

	private GetBarcodeResponse createStubbedGetBarcodeResponse(String serviceFeeType) {
		SIEngineResponse siengineResponse = createBillInfoResponse(serviceFeeType);		
		GetBarcodeResponse billResponse = new GetBarcodeResponse(siengineResponse);
		billResponse.setTransactionID("GetBillInfoID");
		billResponse.setResultCode("0");
		billResponse.setResultNamespace("SIENGINE");
		return billResponse;
	}

	private SIEngineResponse createBillInfoResponse(String serviceFeeType) {
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
		siengineResponse.addParameterElement("service_fee_type", serviceFeeType);
		if ("Percent".equals(serviceFeeType)) {
			siengineResponse.addParameterElement("service_fee", "3");
			siengineResponse.addParameterElement("total_service_fee", "30");
			siengineResponse.addParameterElement("service_max_amount", "20000");
			siengineResponse.addParameterElement("service_min_amount", "000");		
		} else if ("THB".equals(serviceFeeType)) {
			siengineResponse.addParameterElement("service_fee", "10000");
			siengineResponse.addParameterElement("total_service_fee", "10000");
			siengineResponse.addParameterElement("service_max_amount", "20000");
			siengineResponse.addParameterElement("service_min_amount", "100");		
		}
		siengineResponse.addParameterElement("target", "tcg");
		siengineResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
		siengineResponse.addParameterElement("title_th", "ค่าใช้บริการบริษัทในกลุ่มทรู");
		siengineResponse.addParameterElement("title_en", "Convergence Postpay");
		siengineResponse.addParameterElement("ref1_title_th", "โทรศัพท์พื้นฐาน");
		siengineResponse.addParameterElement("ref1_title_en", "Fix Line");
		siengineResponse.addParameterElement("ref1", "010004552");
		siengineResponse.addParameterElement("ref2_title_th", "รหัสลูกค้า");
		siengineResponse.addParameterElement("ref2_title_en", "Customer ID");
		siengineResponse.addParameterElement("ref2", "010520120200015601");		
		siengineResponse.addParameterElement("partial_payment", "");	
		siengineResponse.addParameterElement("call_center", "");	
		siengineResponse.addParameterElement("amount", "10000");	
		siengineResponse.addParameterElement("duedate_bill", "01/01/2013");	
		siengineResponse.addParameterElement("debtstatus_bill", "0");
		return siengineResponse;
	}

	private Bill createBillInfo(BigDecimal amount) {
		Bill billInfo = new Bill("BillID", "tmvh", "08xxxxxxxx", "", amount);
		billInfo.setServiceFee(new ServiceFeeInfo("THB", BigDecimal.TEN));
		billInfo.setSourceOfFundFees(
				new SourceOfFund[] { 
					new SourceOfFund("EW", "THB", BigDecimal.TEN), 
					new SourceOfFund("CreditCard", "Percent", new BigDecimal(3))
				});
		return billInfo;
	}
	
	private VerifyBillPayResponse createStubbedVerifyResponse() {
		VerifyBillPayResponse verifyResponse = new VerifyBillPayResponse(new SIEngineResponse());
		verifyResponse.setTransactionID("verifyID");
		verifyResponse.setResultCode("0");
		verifyResponse.setResultNamespace("SIENGINE");
		return verifyResponse;
	}
	
	private ConfirmBillPayResponse createStubbedConfirmResponse() {
		SIEngineResponse siengineResponse = new SIEngineResponse();
		siengineResponse.addParameterElement("approve_code", "ApproveID");
		ConfirmBillPayResponse confirmResponse = new ConfirmBillPayResponse(siengineResponse);
		confirmResponse.setTransactionID("transactionID");
		confirmResponse.setResultCode("0");
		confirmResponse.setResultNamespace("SIENGINE");
		return confirmResponse;
	}
	
}
