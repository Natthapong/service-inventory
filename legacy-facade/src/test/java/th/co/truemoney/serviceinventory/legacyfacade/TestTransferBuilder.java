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
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransfer;

import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.TransferRequest;
import com.tmn.core.api.message.VerifyTransferRequest;
import com.tmn.core.api.message.VerifyTransferResponse;

public class TestTransferBuilder {

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
	public void verifyTransferMoneySuccess() {
		//given 
		when(walletProxyClientMock.verifyTransfer(any(VerifyTransferRequest.class)))
			.thenReturn(createStubbedVerifyTransferResponse());
		
		//when
		P2PTransfer p2pTransfer = legacyFacade.transfer(BigDecimal.TEN)
				.fromChannelID(accessToken.getChannelID())
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toTargetUser("08xxxxxxx")
				.verify();
		
		//then
		assertNotNull(p2pTransfer);
		assertEquals("recipientName", p2pTransfer.getRecipientName());
		assertEquals("recipientImageFileName", p2pTransfer.getRecipientImageFileName());
		verify(walletProxyClientMock).verifyTransfer(any(VerifyTransferRequest.class));
	}

	@Test(expected=FailResultCodeException.class)
	public void verifyTransferMoneyFailWithResultCode() {
		//given 
		when(walletProxyClientMock.verifyTransfer(any(VerifyTransferRequest.class)))
			.thenThrow(new FailResultCodeException("1","Core"));
		
		//when
		legacyFacade.transfer(BigDecimal.TEN)
			.fromChannelID(accessToken.getChannelID())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toTargetUser("08xxxxxxx")
			.verify();
		
		//then
		verify(walletProxyClientMock).verifyTransfer(any(VerifyTransferRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void verifyTransferMoneyFailWithUnExpectedCode() {
		//given 
		when(walletProxyClientMock.verifyTransfer(any(VerifyTransferRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.transfer(BigDecimal.TEN)
			.fromChannelID(accessToken.getChannelID())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toTargetUser("08xxxxxxx")
			.verify();
		
		//then
		verify(walletProxyClientMock).verifyTransfer(any(VerifyTransferRequest.class));
	}
	
	@Test
	public void transferMoneySuccess() {
		//given 
		when(walletProxyClientMock.transfer(any(TransferRequest.class)))
			.thenReturn(createStubbedStandardMoneyResponse());
		
		//when
		P2PTransactionConfirmationInfo confirmationInfo = legacyFacade
				.fromChannel(accessToken.getChannelID())
				.transfer(BigDecimal.TEN)
				.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.toTargetUser("08xxxxxxxx")
				.setPersonalMessage("personalMessage")
				.performTransfer();
		
		//then
		assertNotNull(confirmationInfo);
		assertEquals("transactionID", confirmationInfo.getTransactionID());
		verify(walletProxyClientMock).transfer(any(TransferRequest.class));
	}

	@Test(expected=FailResultCodeException.class)
	public void transferMoneyFailWithResultCode() {
		//given 
		when(walletProxyClientMock.transfer(any(TransferRequest.class)))
			.thenThrow(new FailResultCodeException("1","Core"));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
			.transfer(BigDecimal.TEN)
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toTargetUser("08xxxxxxxx")
			.setPersonalMessage("personalMessage")
			.performTransfer();
		
		//then
		verify(walletProxyClientMock).transfer(any(TransferRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void transferMoneyFailWithUnExpectedCode() {
		//given 
		when(walletProxyClientMock.transfer(any(TransferRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.fromChannel(accessToken.getChannelID())
			.transfer(BigDecimal.TEN)
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toTargetUser("08xxxxxxxx")
			.setPersonalMessage("personalMessage")
			.performTransfer();
		
		//then
		verify(walletProxyClientMock).transfer(any(TransferRequest.class));
	}
	
	private VerifyTransferResponse createStubbedVerifyTransferResponse() {
		VerifyTransferResponse verifyResponse = new VerifyTransferResponse();
		verifyResponse.setTransactionId("transactionID");
		verifyResponse.setResultCode("0");
		verifyResponse.setResultNamespace("core");
		verifyResponse.setTargetFullname("recipientName");
		verifyResponse.setTargetProfilePicture("recipientImageFileName");
		return verifyResponse;
	}
	
	private StandardMoneyResponse createStubbedStandardMoneyResponse() {
		StandardMoneyResponse standardMoneyResponse = new StandardMoneyResponse();
		standardMoneyResponse.setTransactionId("transactionID");
		standardMoneyResponse.setResultCode("0");
		standardMoneyResponse.setResultNamespace("core");
		return standardMoneyResponse;
	}
	
}
