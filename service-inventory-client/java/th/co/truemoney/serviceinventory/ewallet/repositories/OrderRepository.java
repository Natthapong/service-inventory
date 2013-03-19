package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

public interface OrderRepository {
	public void saveTopUpQuote(TopUpQuote topupQuote);
	public TopUpQuote getTopUpQuote(String orderID);
	public void saveTopUpOrder(TopUpOrder topupOrder);
	public TopUpOrder getTopUpOrder(String orderID);
}
