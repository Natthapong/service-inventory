package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import com.tmn.core.api.message.StandardMoneyResponse;

public class TopUpStubbed {

	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
		StandardMoneyResponse response = new StandardMoneyResponse();
		response.setTransactionId("1234");
		response.setResultCode("0");
		response.setResultNamespace("namespace");
		response.setLoginId("stub@local.com");
		response.setRemainingBalance(new BigDecimal(100.00));
		return response;
	}
	
}
