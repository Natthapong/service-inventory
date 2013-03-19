package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardMoneyResponse;

public class TopUpStubbed {

	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
		return new StandardMoneyResponse("1234", "0", "namespce", new String[] {"key"}, new String[] {"value"}, "stub@local.com", new BigDecimal(100.00));
	}
}
