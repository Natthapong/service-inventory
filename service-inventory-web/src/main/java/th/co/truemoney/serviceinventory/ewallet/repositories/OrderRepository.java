package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface OrderRepository {
	public void saveTopUpQuote(TopUpQuote topupQuote);
	public TopUpQuote getTopUpQuote(String orderID);
	public void saveTopUpOrder(TopUpOrder topupOrder);
	public TopUpOrder getTopUpOrder(String orderID) throws ServiceInventoryException;
	
	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction);
	public P2PDraftTransaction getP2PDraftTransaction(String p2pDraftTransactionID);
}
