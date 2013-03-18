package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TopUpService {

	public TopUpOrder createOrderFromDirectDebit(String sourceOfFundId, BigDecimal amount, String accessToken) throws ServiceInventoryException;

	public TopUpOrder placeOrder(String topupOrderId, String accessToken) throws ServiceInventoryException;

	public TopUpStatus getOrderStatus(String topupOrderId, String accessToken) throws ServiceInventoryException;

	public TopUpOrder getTopupOrderDetails(String topupOrderId, String accessToken) throws ServiceInventoryException;
}
