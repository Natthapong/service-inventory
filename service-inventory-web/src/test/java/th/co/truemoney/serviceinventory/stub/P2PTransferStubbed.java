package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;

public class P2PTransferStubbed {
	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
		return new StandardMoneyResponse("1234", "0", "namespce", new String[] {"key"}, new String[] {"value"}, "stub@local.com", new BigDecimal(100.00));
	}
	public static VerifyTransferResponse createSuccessStubbedVerifyTransferResponse() {
		return new VerifyTransferResponse("1234", "0", "namespce", new String[] {""}, new String[] {""}, "stub@local.com", new BigDecimal(100.00), "target Fullname");
	}
}
