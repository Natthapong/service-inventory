package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;

public class TopUpFacadePerformTopUpSuccessTest {

	private EwalletBalanceHandler topUpFacade;

	private EwalletSoapProxy ewalletProxy;

	private StandardMoneyResponse moneyResponse;

	@Before
	public void setup() {
		ewalletProxy = mock(EwalletSoapProxy.class);

		moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("0");
		moneyResponse.setTransactionId("transId");

		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);

		topUpFacade = new EwalletBalanceHandler(ewalletProxy);
	}

	@Test
	public void topUpSuccess() {
		TopUpConfirmationInfo confirmation = topUpFacade.topUpMoney(new BigDecimal(50),"sourceID", "sourceType", 1, "sessionID", "truemoneyID");
		Assert.assertNotNull(confirmation);
		Assert.assertNotNull(confirmation.getTransactionID());
		Assert.assertNotNull(confirmation.getTransactionDate());
	}
}
