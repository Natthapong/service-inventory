package th.co.truemoney.serviceinventory.ewallet.domain;

import java.math.BigDecimal;

public class TopUpOrder {

	String orderId;
	SourceOfFund sourceOfFund;
	String username;
	TopUpStatus status;
	BigDecimal amount;
	BigDecimal topUpFee;
}
