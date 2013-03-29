package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;

public class AsyncServiceTest {

	private AsyncService asyncService;
	private TransactionRepository orderRepo;
	private EwalletSoapProxy ewalletProxy;
	private TopUpOrder topUpOrderParams;

	@Before
	public void setup() {
		asyncService = new AsyncService();
		orderRepo = new TransactionMemoryRepository();
		ewalletProxy = mock(EwalletSoapProxy.class);

		asyncService.setOrderRepo(orderRepo);
		asyncService.setEwalletProxy(ewalletProxy);

		topUpOrderParams = new TopUpOrder();
		topUpOrderParams.setID("1");
		topUpOrderParams.setStatus(Transaction.Status.PROCESSING);
	}

	@After
	public void teardown() {
		reset(ewalletProxy);
	}

	@Test
	public void topUpUtibaEwallet() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("0");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, "tokenID", new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.SUCCESS, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}

	@Test
	public void topUpUtibaEwalletBankFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("24010");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new EwalletException("24010","umarket"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, "tokenID", new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
		assertEquals(FailStatus.BANK_FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getFailStatus());
	}

	@Test
	public void topUpUtibaEwalletUMarketFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("27");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new EwalletException("27","umarket"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, "tokenID", new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
		assertEquals(FailStatus.UMARKET_FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getFailStatus());
	}

	@Test
	public void topUpUtibaEwalletOthersFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new EwalletException("300",""));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, "tokenID", new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}

	@Test
	public void topUpUtibaEwalletThrowException() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new RuntimeException("test error"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, "tokenID", new AddMoneyRequest());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, orderRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}
}
