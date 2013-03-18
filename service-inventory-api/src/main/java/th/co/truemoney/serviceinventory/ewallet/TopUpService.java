package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;

public interface TopUpService {

	public TopUpOrder createOrderFromDirectDebit(String sourceOfFundId, BigDecimal amount, String accessToken);

	public TopUpOrder placeOrder(String topupOrderId, String accessToken);

	public TopUpStatus getOrderStatus(String topupOrderId, String accessToken);

	public TopUpOrder getTopupOrderDetails(String topupOrderId, String accessToken);
}
