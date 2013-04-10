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
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.TopUpStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TopUpServiceImplVerifyTopUpTest {

	//unit under test
	private TopUpServiceImpl topUpService =  new TopUpServiceImpl();

	private EnhancedDirectDebitSourceOfFundService direcDebitServiceMock = Mockito.mock(EnhancedDirectDebitSourceOfFundService.class);

	private EwalletSoapProxy ewalletProxyMock = Mockito.mock(EwalletSoapProxy.class);

	private AccessToken accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();

	private DirectDebit userDirectDebit = new DirectDebit("sofID", "debit");

	@Before
	public void setup() {

		LegacyFacade legacyFacade = new LegacyFacade();
		legacyFacade.setBalanceFacade(new BalanceFacade(ewalletProxyMock));

		this.topUpService.setLegacyFacade(legacyFacade);
		this.topUpService.setDirectDebitSourceService(direcDebitServiceMock);
		this.topUpService.setOrderRepository(new TransactionMemoryRepository());

		AccessTokenMemoryRepository accessTokenRepo = new AccessTokenMemoryRepository();
		this.topUpService.setAccessTokenRepository(accessTokenRepo);

		//given
		accessTokenRepo.save(accessToken);

		when(ewalletProxyMock.verifyAddMoney(Mockito.any(VerifyAddMoneyRequest.class)))
			.thenReturn(TopUpStubbed.createSuccessStubbedStandardMoneyResponse());

		when(direcDebitServiceMock.getUserDirectDebitSource(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(userDirectDebit);
	}

	@Test
	public void verifyAndCreateTopUpQuoteSuccess() {

		//given
		BigDecimal amount = new BigDecimal(400);

		//when
		TopUpQuote topupQuote = this.topUpService.verifyAndCreateTopUpQuote(userDirectDebit.getSourceOfFundID(), amount, accessToken.getAccessTokenID());

		//then
		assertNotNull(topupQuote);
	}

	@Test
	public void verifyAndCreateTopUpQuoteSuccessFailLessThanMinAmount() {

		//given
		BigDecimal topUpAmount = new BigDecimal(30);
		userDirectDebit.setMinAmount(new BigDecimal(300));

		//when
		try {
			this.topUpService.verifyAndCreateTopUpQuote(userDirectDebit.getSourceOfFundID(), topUpAmount, accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ServiceInventoryWebException e) {
			//then
			assertEquals("20001", e.getErrorCode());
		}
	}

	@Test
	public void verifyAndCreateTopUpQuoteSuccessFailMostThanMaxAmount() {

		//given
		BigDecimal topUpAmount = new BigDecimal(50000);
		userDirectDebit.setMaxAmount(new BigDecimal(30000));

		//when
		try {
			this.topUpService.verifyAndCreateTopUpQuote(userDirectDebit.getSourceOfFundID(), topUpAmount, accessToken.getAccessTokenID());
			Assert.fail();
		} catch (ServiceInventoryWebException e) {
			//then
			assertEquals("20002", e.getErrorCode());
		}
	}

}
