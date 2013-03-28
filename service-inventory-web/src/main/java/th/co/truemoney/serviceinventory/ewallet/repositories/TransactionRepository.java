package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TransactionRepository {
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote);
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID);
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder);
	public TopUpOrder getTopUpEwalletTransaction(String orderID) throws ServiceInventoryException;
	
	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction);
	public P2PDraftTransaction getP2PDraftTransaction(String p2pDraftTransactionID);
}
