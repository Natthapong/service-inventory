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
import org.junit.experimental.categories.Category;

import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

import com.tmn.core.api.message.AddMoneyRequest;
import com.tmn.core.api.message.StandardMoneyResponse;

@Category(IntegrationTest.class)
public class AsyncTopUpEwalletProcessorTest {

    // unit under test
    private AsyncTopUpEwalletProcessor asyncService = new AsyncTopUpEwalletProcessor();

    private TransactionRepository transactionRepo = new TransactionRepositoryImpl(new MemoryExpirableMap());

    private WalletProxyClient mockEwalletProxy = mock(WalletProxyClient.class);

    private AccessToken accessToken = new AccessToken("tokenID", "loginID", "sessionID", "tmnID", 41);

    private TopUpOrder incomingOrder;

    @Before
    public void setup() {

        LegacyFacade legacyFacade = new LegacyFacade();
        EwalletBalanceHandler ewalletBalanceHandler = new EwalletBalanceHandler();
        ewalletBalanceHandler.setWalletProxyClient(mockEwalletProxy);
        legacyFacade.setBalanceFacade(ewalletBalanceHandler);

        asyncService.setLegacyFacade(legacyFacade);
        asyncService.setTransactionRepo(transactionRepo);


        incomingOrder = setUpIncomingOrder();

        transactionRepo.saveTransaction(incomingOrder, "tokenID");
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
    public void topUpUtibaEwalletUnknownFail() {

        //given
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("24010","bank fail"));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
        assertEquals(FailStatus.UNKNOWN_FAILED, valueFromRepo.getFailStatus());
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

        //given
        when(mockEwalletProxy.addMoney(any(AddMoneyRequest.class))).thenThrow(new FailResultCodeException("500", "fail"));

        //when
        Future<TopUpOrder> futureResult = asyncService.topUpUtibaEwallet(incomingOrder, accessToken);

        //then
        TopUpOrder valueFromRepo = getTransactionFromRepoByID(incomingOrder.getID(), accessToken.getAccessTokenID());

        assertEquals(true, futureResult.isDone());
        assertEquals(TopUpOrder.Status.FAILED, valueFromRepo.getStatus());
    }

    private TopUpOrder getTransactionFromRepoByID(String transactionID, String accessTokenID) {
        return transactionRepo.findTransaction(transactionID, accessTokenID, TopUpOrder.class);
    }
}
