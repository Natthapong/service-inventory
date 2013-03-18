package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;

public class OrderMemoryRepository implements OrderRepository {

	public static HashMap<String, TopUpQuote> mapQuote = new LinkedHashMap<String, TopUpQuote>();
	public static HashMap<String, TopUpOrder> mapOrder = new LinkedHashMap<String, TopUpOrder>();
	
	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote) {
		mapQuote.put(topupQuote.getId(), topupQuote);
	}

	@Override
	public TopUpQuote getTopUpQuote(String orderID) {
		return mapQuote.get(orderID);
	}
	
	@Override
	public void saveTopUpOrder(TopUpOrder topupOrder) {
		mapOrder.put(topupOrder.getId(), topupOrder);
	}

	@Override
	public TopUpOrder getTopUpOrder(String orderID) {
		return mapOrder.get(orderID);
	}

	

}
