package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.P2PTransferStubbed;

@RunWith(MockitoJUnitRunner.class)
public class P2PTransferServiceImplTest {

	private P2PTransferServiceImpl p2pService;
	private EwalletSoapProxy ewalletSoapProxyMock;
	private AccessTokenRepository accessTokenRepoMock;
	
	
	@Before
	public void setup() {
		this.p2pService = new P2PTransferServiceImpl();
		this.ewalletSoapProxyMock = Mockito.mock(EwalletSoapProxy.class);
		this.accessTokenRepoMock = Mockito.mock(AccessTokenRepository.class);


		this.p2pService.setEwalletProxy(this.ewalletSoapProxyMock);
		this.p2pService.setAccessTokenRepository(this.accessTokenRepoMock);
		this.p2pService.setTransactionRepository(new TransactionMemoryRepository());
	}

	@Test
	public void createDraftTransactionSuccess() {	
		P2PDraftRequest p2pDraftRequest = new P2PDraftRequest();
		p2pDraftRequest.setAmount(new BigDecimal(200));
		p2pDraftRequest.setMobileNumber("0811111111");
		
		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		StandardMoneyResponse stubbedStandardMoneyResponse = P2PTransferStubbed.createSuccessStubbedStandardMoneyResponse();
		
		//when
		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
			.thenReturn(accessToken);
		when(ewalletSoapProxyMock.verifyTransfer(Mockito.any(VerifyTransferRequest.class)))
			.thenReturn(stubbedStandardMoneyResponse);
	
		P2PDraftTransaction draftTrans = this.p2pService.createDraftTransaction(p2pDraftRequest, accessToken.getAccessTokenID());

		//then
		assertNotNull(draftTrans);
	}
}
