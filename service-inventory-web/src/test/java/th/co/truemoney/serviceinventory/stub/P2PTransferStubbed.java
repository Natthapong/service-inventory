package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;

public class P2PTransferStubbed {
	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
		return new StandardMoneyResponse("1234", "0", "namespce", new String[] {"fullName"}, new String[] {"john doexxxx"}, "stub@local.com", new BigDecimal(100.00));
	}
}
