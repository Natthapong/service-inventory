package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;

public class AsyncTopUpEwalletProcessorTest {

	private AsyncTopUpEwalletProcessor asyncService;
	private TransactionRepository transactionRepo;
	private EwalletSoapProxy ewalletProxy;
	private TopUpOrder topUpOrderParams;

	@Before
	public void setup() {
		asyncService = new AsyncTopUpEwalletProcessor();
		transactionRepo = new TransactionMemoryRepository();
		ewalletProxy = mock(EwalletSoapProxy.class);

		BalanceFacade facade = new BalanceFacade();
		facade.setEwalletProxy(ewalletProxy);

		asyncService.setTransactionRepo(transactionRepo);
		asyncService.setTopUpFacade(new BalanceFacade.TopUpBuilder(facade));

		topUpOrderParams = new TopUpOrder();
		topUpOrderParams.setID("1");
		topUpOrderParams.setStatus(Transaction.Status.PROCESSING);

		TopUpQuote quote = new TopUpQuote("1", new DirectDebit(), "tokenID", "", new BigDecimal(400), new BigDecimal(30));
		topUpOrderParams.setQuote(quote);

		transactionRepo.saveTopUpEwalletTransaction(topUpOrderParams, "tokenID");
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

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AccessToken());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.SUCCESS, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}

	@Test
	public void topUpUtibaEwalletBankFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("24010");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("24010","bank fail"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AccessToken("tokenID"));
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
		assertEquals(FailStatus.BANK_FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getFailStatus());
	}

	@Test
	public void topUpUtibaEwalletUMarketFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("27");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("27","umarket fail"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams,  new AccessToken());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
		assertEquals(FailStatus.UMARKET_FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getFailStatus());
	}

	@Test
	public void topUpUtibaEwalletOthersFail() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("300",""));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams,  new AccessToken());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}

	@Test
	public void topUpUtibaEwalletThrowException() {
		StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		moneyResponse.setResultCode("300");
		when(ewalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new RuntimeException("test error"));

		Future<TopUpOrder> topUpOrder = asyncService.topUpUtibaEwallet(topUpOrderParams, new AccessToken());
		assertEquals(true, topUpOrder.isDone());
		assertEquals(Transaction.Status.FAILED, transactionRepo.getTopUpEwalletTransaction(topUpOrderParams.getID(), "tokenID").getStatus());
	}
}
