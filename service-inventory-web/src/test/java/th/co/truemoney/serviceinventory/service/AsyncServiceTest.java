package th.co.truemoney.serviceinventory.service;

import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;


import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;

public class AsyncServiceTest {
	
	private AsyncService asyncService;
	private OrderRepository orderRepo;
	private EwalletSoapProxy ewalletProxy;
	private TopUpOrder topUpOrderParams;
	
	@Before
	public void setup() {
		asyncService = new AsyncService();
		orderRepo = new OrderMemoryRepository();
		ewalletProxy = mock(EwalletSoapProxy.class);
		
		asyncService.setOrderRepo(orderRepo);
		asyncService.setEwalletProxy(ewalletProxy);
		
		topUpOrderParams = new TopUpOrder();
		topUpOrderParams.setID("1");
		topUpOrderParams.setStatus(TopUpStatus.PROCESSING);
	}
	
	@Test
	public void topUpUtibaEwallet() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("0");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);		
		
		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams , new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(TopUpStatus.CONFIRMED, orderRepo.getTopUpOrder(topUpOrderParams.getID()).getStatus());
	}
	
	@Test
	public void topUpUtibaEwalletBankFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("24010");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);		
		
		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(TopUpStatus.BANK_FAILED, orderRepo.getTopUpOrder(topUpOrderParams.getID()).getStatus());
	}
	
	@Test
	public void topUpUtibaEwalletUMarketFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("27");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);		
		
		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(TopUpStatus.UMARKET_FAILED, orderRepo.getTopUpOrder(topUpOrderParams.getID()).getStatus());
	}
	
	@Test
	public void topUpUtibaEwalletOthersFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);		
		
		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(TopUpStatus.FAILED, orderRepo.getTopUpOrder(topUpOrderParams.getID()).getStatus());
	}
	
	@Test
	public void topUpUtibaEwalletThrowException() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new RuntimeException("test error"));		
		
		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(TopUpStatus.FAILED, orderRepo.getTopUpOrder(topUpOrderParams.getID()).getStatus());
	}
}
