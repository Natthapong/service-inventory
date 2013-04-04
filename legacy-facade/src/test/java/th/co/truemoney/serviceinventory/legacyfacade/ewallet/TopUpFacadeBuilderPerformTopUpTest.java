package th.co.truemoney.serviceinventory.legacyfacade.ewallet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade.TopUpBuilder;


public class TopUpFacadeBuilderPerformTopUpTest {

	private TopUpBuilder topUpFacadeBuilder;
	private BalanceFacade facadeMock;

	@Before
	public void setup() {
		facadeMock = mock(BalanceFacade.class);
		topUpFacadeBuilder = new BalanceFacade.TopUpBuilder(facadeMock);
	}

	@Test
	public void performTopUpSuccess() {
		topUpFacadeBuilder.withAmount(new BigDecimal(20))
		.usingSourceOfFund(new DirectDebit())
		.fromUser(new AccessToken("abcd"))
		.performTopUp();

		verify(facadeMock).topUpMoney(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void performTopUpFailWhenMissingAccessToken() {
		try {
			topUpFacadeBuilder.withAmount(new BigDecimal(20))
			.usingSourceOfFund(new DirectDebit())
			.performTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void performTopUpFailWhenMissingSourceOfFund() {
		try {
			topUpFacadeBuilder.withAmount(new BigDecimal(20))
			.fromUser(new AccessToken())
			.performTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void performTopUpFailWhenMissingTopUpAmount() {
		try {
			topUpFacadeBuilder.fromUser(new AccessToken())
			.performTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

}
