package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DirectDebitOrderResult implements Serializable {

	private static final long serialVersionUID = -3136107024016038883L;

	
	private String transactionID;
	private String bankAccountNumber;
	private BigDecimal fee;
	private BigDecimal currentBallance;
}
