package th.co.truemoney.serviceinventory.legacyfacade.ewallet;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpFacade.DSLBuilder;


public class TopUpFacadeBuilderVerifyTopUpTest {

	private DSLBuilder topUpFacadeBuilder;
	private TopUpFacade facadeMock;

	@Before
	public void setup() {
		facadeMock = mock(TopUpFacade.class);
		topUpFacadeBuilder = new TopUpFacade.DSLBuilder(facadeMock);
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
