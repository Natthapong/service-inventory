package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler.BankSystemTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler.UMarketSystemTransactionFailException;

@RunWith(Parameterized.class)
public class TopUpFacadePerformTopUpFailTest {

	private EwalletBalanceHandler topUpFacade;

	private EwalletSoapProxy ewalletProxy;

	private String resultCode;
	private Class<? extends ServiceInventoryException> expectException;


	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "24003", BankSystemTransactionFailException.class },
				{ "24008", BankSystemTransactionFailException.class },
				{ "24010", BankSystemTransactionFailException.class },
				{ "25007", BankSystemTransactionFailException.class },
				{ "5", UMarketSystemTransactionFailException.class },
				{ "6", UMarketSystemTransactionFailException.class },
				{ "7", UMarketSystemTransactionFailException.class },
				{ "19", UMarketSystemTransactionFailException.class },
				{ "27", UMarketSystemTransactionFailException.class },
				{ "38", UMarketSystemTransactionFailException.class },
			};
		return Arrays.asList(data);
	}

	public TopUpFacadePerformTopUpFailTest(String resultCode, Class<? extends ServiceInventoryException> exClazz) {
		this.resultCode = resultCode;
		this.expectException = exClazz;
	}

	@Before
	public void setup() {
		ewalletProxy = mock(EwalletSoapProxy.class);

		FailResultCodeException failResultCodeException = new FailResultCodeException(resultCode, "HELLO");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(failResultCodeException);

		topUpFacade = new EwalletBalanceHandler(ewalletProxy);
	}

	@Test
	public void topUpShouldThrowCorrectSystemFailExceptions() {

		try {
			topUpFacade.topUpMoney(new BigDecimal(50),"sourceID", "sourceType", 1, "sessionID", "truemoneyID");
			Assert.fail();
		} catch(ServiceInventoryException ex) {
			Assert.assertEquals(ex.getClass(), expectException);
		}
	}
}
