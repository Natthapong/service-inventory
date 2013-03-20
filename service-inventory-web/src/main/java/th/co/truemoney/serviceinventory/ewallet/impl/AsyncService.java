package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
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
	
	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, AccessToken accessToken) {
		logger.debug("call ewalletProxy");
		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(topUpOrder.getAmount());
		addMoneyRequest.setChannelId(accessToken.getChannelID());		
		addMoneyRequest.setRequestTransactionId(topUpOrder.getConfirmationInfo().getTransactionID());
		addMoneyRequest.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));
		addMoneyRequest.setSourceId(topUpOrder.getDirectDebit().getSourceOfFundID());
		addMoneyRequest.setSourceType(topUpOrder.getDirectDebit().getSourceOfFundType());
		
		StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);		
		
		if(moneyResponse.getResultCode().equals("0")) {
			topUpOrder.setStatus(TopUpStatus.CONFIRMED);
			
		} else {
			topUpOrder.setStatus(TopUpStatus.FAILED);
		} 
		
		orderRepo.saveTopUpOrder(topUpOrder);
		
		return new AsyncResult<TopUpOrder> (topUpOrder);
	}
	
}
