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
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpFacade.DSLBuilder;


public class TopUpFacadeBuilderPerformTopUpTest {

	private DSLBuilder topUpFacadeBuilder;
	private TopUpFacade facadeMock;

	@Before
	public void setup() {
		facadeMock = mock(TopUpFacade.class);
		topUpFacadeBuilder = new TopUpFacade.DSLBuilder(facadeMock);
	}

	@Test
	public void performTopUpSuccess() {
		topUpFacadeBuilder.withAmount(new BigDecimal(20))
		.usingSourceOfFund(new DirectDebit())
		.fromUser(new AccessToken("abcd"))
		.performTopUp();

		verify(facadeMock).verifyToppingUpCapability(any(BigDecimal.class), any(SourceOfFund.class), any(AccessToken.class));
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
