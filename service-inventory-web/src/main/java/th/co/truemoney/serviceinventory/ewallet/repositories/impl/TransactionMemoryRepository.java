package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TransactionMemoryRepository implements TransactionRepository {

	public static HashMap<String, TopUpQuote> quotesMap = new LinkedHashMap<String, TopUpQuote>();
	public static HashMap<String, TopUpOrder> ordersMap = new LinkedHashMap<String, TopUpOrder>();
	public static HashMap<String, P2PDraftTransaction> p2pDraftTransactionMap = new LinkedHashMap<String, P2PDraftTransaction>();
	public static HashMap<String, P2PTransaction> p2pTransactionMap = new LinkedHashMap<String, P2PTransaction>();

	@Override
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote) {
		quotesMap.put(topupQuote.getID(), topupQuote);
	}

	@Override
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID) {
		return quotesMap.get(orderID);
	}

	@Override
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder) {
		ordersMap.put(topupOrder.getID(), topupOrder);
	}

	@Override
	public TopUpOrder getTopUpEwalletTransaction(String orderID) throws ServiceInventoryException {
		TopUpOrder topUpOrder = ordersMap.get(orderID);
		if(topUpOrder == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TRANSACTION_NOT_FOUND,
					"TopUp order not found.");
		}
		return topUpOrder;
	}

	@Override
	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction) {
		p2pDraftTransactionMap.put(p2pDraftTransaction.getID(), p2pDraftTransaction);
	}

	@Override
	public P2PDraftTransaction getP2PDraftTransaction(
			String p2pDraftTransactionID) {
		P2PDraftTransaction p2pDraftTransaction = p2pDraftTransactionMap.get(p2pDraftTransactionID);
		
		if (p2pDraftTransaction == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.DRAFT_TRANSACTION_NOT_FOUND,
					"TopUp order not found.");
		}
		
		return p2pDraftTransaction;
	}

	@Override
	public void saveP2PTransaction(P2PTransaction p2pTransaction) {
		p2pTransactionMap.put(p2pTransaction.getID(), p2pTransaction);		
	}

	@Override
	public P2PTransaction getP2PTransaction(String p2pTransactionID) {
		P2PTransaction p2pTransaction = p2pTransactionMap.get(p2pTransactionID);
		if(p2pTransaction == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TRANSACTION_NOT_FOUND,
					"Transfer order not found.");
		}
		return p2pTransaction;
	}

}
