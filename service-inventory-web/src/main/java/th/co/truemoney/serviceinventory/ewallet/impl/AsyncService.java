package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.Date;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class AsyncService {
	private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
	@Qualifier("orderRedisRepository")
	private OrderRepository orderRepo;
	
	@Autowired
	private EwalletSoapProxy ewalletProxy;
	
	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, AddMoneyRequest addMoneyRequest) {
		logger.debug("start time " + new Date());
		StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);
		logger.debug("finished time " + new Date());
		if(moneyResponse.getResultCode().equals("0")) {
			topUpOrder.setStatus(TopUpStatus.CONFIRMED);			
		} else {
			topUpOrder.setStatus(TopUpStatus.FAILED);
			// 24003, 24008, 24010, 25007 BANK_FAIL			
			// 5, 6, 7, 19, 27, 38 UMARKET_FAIL
		} 
		
		orderRepo.saveTopUpOrder(topUpOrder);
		
		return new AsyncResult<TopUpOrder> (topUpOrder);
	}
	
}
