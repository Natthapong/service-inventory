package th.co.truemoney.serviceinventory.legacyfacade.ewallet;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade.TopUpBuilder;


public class TopUpFacadeBuilderVerifyTopUpTest {

	private TopUpBuilder topUpFacadeBuilder;
	private BalanceFacade facadeMock;

	@Before
	public void setup() {
		facadeMock = mock(BalanceFacade.class);
		topUpFacadeBuilder = new BalanceFacade.TopUpBuilder(facadeMock);
	}

	@Test
	public void verifyTopUpSuccess() {
		topUpFacadeBuilder.withAmount(new BigDecimal(20))
		.usingSourceOfFund(new DirectDebit())
		.fromUser(new AccessToken("abcd"))
		.verifyTopUp();

		verify(facadeMock).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void verifyTopUpFailWhenMissingAccessToken() {
		try {
			topUpFacadeBuilder.withAmount(new BigDecimal(20))
			.usingSourceOfFund(new DirectDebit())
			.verifyTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void verifyTopUpFailWhenMissingSourceOfFund() {
		try {
			topUpFacadeBuilder.withAmount(new BigDecimal(20))
			.fromUser(new AccessToken())
			.verifyTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}

	@Test
	public void verifyTopUpFailWhenMissingTopUpAmount() {
		try {
			topUpFacadeBuilder.fromUser(new AccessToken())
			.verifyTopUp();

			Assert.fail();
		} catch (Exception ex) {}

		verify(facadeMock, never()).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
	}
}
