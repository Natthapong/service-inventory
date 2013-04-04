package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.EnhancedDirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.TopUpStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TopUpServiceImplVerifyTopUpTest {


	private TopUpServiceImpl topUpService;

	private String sourceOfFundID = "11111111";
	private String accessTokenID;

	@Before
	public void setup() {
		this.topUpService = new TopUpServiceImpl();

		EnhancedDirectDebitSourceOfFundService direcDebitServiceMock = Mockito.mock(EnhancedDirectDebitSourceOfFundService.class);
		EwalletSoapProxy ewalletProxyMock = Mockito.mock(EwalletSoapProxy.class);

		BalanceFacade topUpFacade = new BalanceFacade();
		topUpFacade.setEwalletProxy(ewalletProxyMock);

		AccessTokenMemoryRepository accessTokenRepo = new AccessTokenMemoryRepository();

		this.topUpService.setTopUpFacadeBuilder(topUpFacade.setupTopUp());
		this.topUpService.setDirectDebitSourceService(direcDebitServiceMock);
		this.topUpService.setAccessTokenRepository(accessTokenRepo);
		this.topUpService.setOrderRepository(new TransactionMemoryRepository());

		DirectDebit directDebitDetail = new DirectDebit();
		directDebitDetail.setBankAccountNumber("xxxx5555");
		directDebitDetail.setBankCode("SCB");
		directDebitDetail.setBankNameEn("Siam Comercial Bank");
		directDebitDetail.setMinAmount(new BigDecimal(300));
		directDebitDetail.setMaxAmount(new BigDecimal(30000));
		directDebitDetail.setSourceOfFundID(sourceOfFundID);

		//given
		AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		accessTokenRepo.save(accessToken);
		accessTokenID = accessToken.getAccessTokenID();

		when(ewalletProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(TopUpStubbed.createSuccessStubbedStandardMoneyResponse());

		when(direcDebitServiceMock.getUserDirectDebitSource(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(directDebitDetail);
	}

	@Test
	public void createTopUpQuoteFromDirectDebitSuccess() {

		//given
		BigDecimal amount = new BigDecimal(400);

		//when
		TopUpQuote topupQuote = this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);

		//then
		assertNotNull(topupQuote);
	}

	@Test
	public void createTopUpQuoteFromDirectDebitFailLessThanMinAmount() {

		//given
		BigDecimal amount = new BigDecimal(30);

		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);
			Assert.fail();
		} catch (ServiceInventoryWebException e) {
			//then
			assertEquals("20001", e.getErrorCode());
		}
	}

	@Test
	public void createTopUpQuoteFromDirectDebitFailMostThanMaxAmount() {

		//given
		BigDecimal amount = new BigDecimal(50000);

		//when
		try {
			this.topUpService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);
			Assert.fail();
		} catch (ServiceInventoryWebException e) {
			//then
			assertEquals("20002", e.getErrorCode());
		}
	}

}
