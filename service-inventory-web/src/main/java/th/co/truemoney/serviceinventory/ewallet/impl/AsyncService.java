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

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;

@Service
public class AsyncService {
	private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
	@Qualifier("orderRedisRepository")
	private OrderRepository orderRepo;
	
	@Autowired
	private EwalletSoapProxy ewalletProxy;
	
	public OrderRepository getOrderRepo() {
		return orderRepo;
	}

	public void setOrderRepo(OrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, AddMoneyRequest addMoneyRequest) {
		try {
			logger.debug("start time " + new Date());
			StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);
			logger.debug("finished time " + new Date());
			
			if (moneyResponse != null) {
				TopUpConfirmationInfo info = new TopUpConfirmationInfo();
				info.setTransactionID(moneyResponse.getTransactionId());
		        Date date = new Date();
		        java.text.SimpleDateFormat df= new java.text.SimpleDateFormat();
		        df.applyPattern("dd/MM/yyyy HH:mm");
				info.setTransactionDate(df.format(date));
				topUpOrder.setConfirmationInfo(info);
			}
			
			String resultCode = moneyResponse.getResultCode();
			if(resultCode.equals("0")) {
				topUpOrder.setStatus(TopUpStatus.CONFIRMED);			
			} else if (resultCode.equals("24003") || resultCode.equals("24008") || 
					resultCode.equals("24010") || resultCode.equals("25007")) {
				topUpOrder.setStatus(TopUpStatus.BANK_FAILED);
			} else if (resultCode.equals("5") || resultCode.equals("6") || 
					resultCode.equals("7") || resultCode.equals("19") || 
					resultCode.equals("27") || resultCode.equals("38")) {
				topUpOrder.setStatus(TopUpStatus.UMARKET_FAILED);
			}else {
				topUpOrder.setStatus(TopUpStatus.FAILED);			
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			topUpOrder.setStatus(TopUpStatus.FAILED);
		}
		
		orderRepo.saveTopUpOrder(topUpOrder);
		
		return new AsyncResult<TopUpOrder> (topUpOrder);
	}

}
