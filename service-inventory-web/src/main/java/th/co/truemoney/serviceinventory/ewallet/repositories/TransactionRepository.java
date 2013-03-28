package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

public interface TransactionRepository {
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote);
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID);
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder);
	public TopUpOrder getTopUpEwalletTransaction(String orderID);
	
	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction);
	public P2PDraftTransaction getP2PDraftTransaction(String p2pDraftTransactionID);
	public void saveP2PTransaction(P2PTransaction p2pTransaction);
	public P2PTransaction getP2PTransaction(String p2pTransactionID);
}
