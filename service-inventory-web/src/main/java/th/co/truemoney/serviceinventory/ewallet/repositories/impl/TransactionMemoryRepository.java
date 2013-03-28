package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TransactionMemoryRepository implements TransactionRepository {

	public static HashMap<String, TopUpQuote> quotesMap = new LinkedHashMap<String, TopUpQuote>();
	public static HashMap<String, TopUpOrder> ordersMap = new LinkedHashMap<String, TopUpOrder>();

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
			throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_ORDER_NOT_FOUND,
					"TopUp order not found.");
		}
		return topUpOrder;
	}



}
