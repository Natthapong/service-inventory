package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

public interface TransactionRepository {
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote, String accessTokenID);
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID, String accessTokenID);
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder, String accessTokenID);
	public TopUpOrder getTopUpEwalletTransaction(String orderID, String accessTokenID);

	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction, String accessTokenID);
	public P2PDraftTransaction getP2PDraftTransaction(String p2pDraftTransactionID, String accessTokenID);
	public void saveP2PTransaction(P2PTransaction p2pTransaction, String accessTokenID);
	public P2PTransaction getP2PTransaction(String p2pTransactionID, String accessTokenID);
}
