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
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade.BankSystemTransactionFailException;

public class AsyncTopUpEwalletProcessorTest {

    // unit under test
    private AsyncTopUpEwalletProcessor asyncService = new AsyncTopUpEwalletProcessor();

    private TransactionRepository transactionRepo = new TransactionMemoryRepository();

    private EwalletSoapProxy mockEwalletProxy = mock(EwalletSoapProxy.class);

    private AccessToken accessToken = new AccessToken("tokenID", "sessionID", "tmnID", 41);

    private TopUpOrder incomingOrder;



    @Before
    public void setup() {

        LegacyFacade legacyFacade = new LegacyFacade();
        legacyFacade.setBalanceFacade(new BalanceFacade(mockEwalletProxy));

        asyncService.setLegacyFacade(legacyFacade);
        asyncService.setTransactionRepo(transactionRepo);


        incomingOrder = setUpIncomingOrder();

        transactionRepo.saveTopUpOrder(incomingOrder, "tokenID");
    }

    private TopUpOrder setUpIncomingOrder() {

        BigDecimal amount = new BigDecimal(400);
        BigDecimal fee = new BigDecimal(30);
        DirectDebit sourceOfFund = new DirectDebit("sourceID", "debit");

        TopUpQuote quote = new TopUpQuote("1", sourceOfFund, accessToken.getAccessTokenID(), amount, fee);
        quote.setStatus(TopUpQuote.Status.OTP_CONFIRMED);

        TopUpOrder order = new TopUpOrder(quote);
        order.setStatus(TopUpOrder.Status.PROCESSING);

        return order;
    }

    @After
    public void teardown() {
        reset(mockEwalletProxy);
    }

    @Test
    public void topUpUtibaEwallet() {

        //given
        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
        moneyResponse.setResultCode("0");
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenReturn(moneyResponse);

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.SUCCESS, valueFromRepo.getStatus());
    }

    @Test
    public void topUpUtibaEwalletBankFail() {

        //given
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("24010","bank fail"));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
        assertEquals(FailStatus.BANK_FAILED, valueFromRepo.getFailStatus());
    }

    @Test
    public void topUpUtibaEwalletUMarketFail() {

        //given
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("27","umarket fail"));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), "tokenID");

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
        assertEquals(FailStatus.UMARKET_FAILED, valueFromRepo.getFailStatus());
    }

    @Test
    public void topUpUtibaEwalletOthersFail() {

        //given
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("300",""));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
    }

    @Test
    public void topUpUtibaEwalletThrowException() {

        //given‰
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new BankSystemTransactionFailException(new FailResultCodeException("500", "fail")));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
    }

    private TopUpOrder getTransactionFromRepoByID(String transactionID, String accessTokenID) {
        return transactionRepo.findTopUpOrder(transactionID, accessTokenID);
    }
}
