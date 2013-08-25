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

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;

import com.tmn.core.api.message.AddMoneyRequest;
import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.VerifyAddMoneyRequest;

public class TestTopupEwalletBuilder {
	
	private LegacyFacade legacyFacade;
	private EwalletBalanceHandler ewalletBalanceFacade;
	private AccessToken accessToken;
	private WalletProxyClient walletProxyClientMock;
	
	@Before
	public void before() {
        this.accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
		this.walletProxyClientMock = mock(WalletProxyClient.class);
		
		this.legacyFacade = new LegacyFacade();
		this.ewalletBalanceFacade = new EwalletBalanceHandler();
		this.ewalletBalanceFacade.setWalletProxyClient(walletProxyClientMock);
		this.legacyFacade.setBalanceFacade(ewalletBalanceFacade);
	}
	
	@After
	public void after() {
		reset(walletProxyClientMock);
	}
	
	@Test
	public void verifyAddMoneySuccess() {
		//given 
		when(walletProxyClientMock.verifyAddMoney(any(VerifyAddMoneyRequest.class))).thenReturn(createStubbedStandardMoneyResponse());
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
			.topUp(BigDecimal.TEN)
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
			.verify();
		
		//then
		verify(walletProxyClientMock).verifyAddMoney(any(VerifyAddMoneyRequest.class));
	}
	
	@Test(expected=FailResultCodeException.class)
	public void verifyAddMoneyFailWithResultCode() {
		//given 
		when(walletProxyClientMock.verifyAddMoney(any(VerifyAddMoneyRequest.class)))
			.thenThrow(new FailResultCodeException("1","Core"));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
			.topUp(BigDecimal.TEN)
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
			.verify();
		
		//then
		verify(walletProxyClientMock).verifyAddMoney(any(VerifyAddMoneyRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void verifyAddMoneyFailWithUnExpectedCode() {
		//given 
		when(walletProxyClientMock.verifyAddMoney(any(VerifyAddMoneyRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
			.topUp(BigDecimal.TEN)
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
			.verify();
		
		//then
		verify(walletProxyClientMock).verifyAddMoney(any(VerifyAddMoneyRequest.class));
	}

	@Test
	public void confirmAddMoneySuccess() {
		//given 
		when(walletProxyClientMock.addMoney(any(AddMoneyRequest.class))).thenReturn(createStubbedStandardMoneyResponse());
		
		//when
		TopUpConfirmationInfo confirmationInfo =
				legacyFacade.fromChannel(accessToken.getChannelID())
				.topUp(BigDecimal.TEN)
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
				.performTopUp();
		
		//then
		assertNotNull(confirmationInfo);
		assertEquals("transactionID", confirmationInfo.getTransactionID());
		verify(walletProxyClientMock).addMoney(any(AddMoneyRequest.class));
	}
	
	@Test(expected=FailResultCodeException.class)
	public void confirmAddMoneyFailWithResultCode() {
		//given 
		when(walletProxyClientMock.addMoney(any(AddMoneyRequest.class)))
			.thenThrow(new FailResultCodeException("1","Core"));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
				.topUp(BigDecimal.TEN)
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
				.performTopUp();
		
		//then
		verify(walletProxyClientMock).addMoney(any(AddMoneyRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void confirmAddMoneyFailWithUnExpectedCode() {
		//given 
		when(walletProxyClientMock.addMoney(any(AddMoneyRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
				.topUp(BigDecimal.TEN)
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.usingSourceOFFund("SourceOfFundID", "SourceOfFundType")
				.performTopUp();
		
		//then
		verify(walletProxyClientMock).addMoney(any(AddMoneyRequest.class));
	}
	
	private StandardMoneyResponse createStubbedStandardMoneyResponse() {
		StandardMoneyResponse standardMoneyResponse = new StandardMoneyResponse();
		standardMoneyResponse.setTransactionId("transactionID");
		standardMoneyResponse.setResultCode("0");
		standardMoneyResponse.setResultNamespace("core");
		return standardMoneyResponse;
	}
	
}
