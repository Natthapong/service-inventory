package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class OrderMemoryRepository implements OrderRepository {

	public static HashMap<String, TopUpQuote> mapQuote = new LinkedHashMap<String, TopUpQuote>();
	public static HashMap<String, TopUpOrder> mapOrder = new LinkedHashMap<String, TopUpOrder>();
	
	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote) {
		mapQuote.put(topupQuote.getID(), topupQuote);
	}

	@Override
	public TopUpQuote getTopUpQuote(String orderID) {
		return mapQuote.get(orderID);
	}
	
	@Override
	public void saveTopUpOrder(TopUpOrder topupOrder) {
		mapOrder.put(topupOrder.getID(), topupOrder);
	}

	@Override
	public TopUpOrder getTopUpOrder(String orderID) throws ServiceInventoryException {
		TopUpOrder topUpOrder = mapOrder.get(orderID);
		if(topUpOrder == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_ORDER_NOT_FOUND,
					"TopUp order not found.");
		}
		return topUpOrder;
	}

	

}
