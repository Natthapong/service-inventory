package th.co.truemoney.serviceinventory.persona.proxies;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.TransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;

public class LocalEwalletSoapProxy implements EwalletSoapProxy {

    @Override
    public StandardMoneyResponse verifyAddMoney(
            VerifyAddMoneyRequest verifyAddMoneyRequest)
            throws EwalletException {
        return new StandardMoneyResponse("1234", "0", "namespce",
                new String[] { "key" }, new String[] { "value" },
                "stub@local.com", new BigDecimal(100.00));
    }

    @Override
    public GetBalanceResponse getBalance(
            th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
            throws EwalletException {
        return new GetBalanceResponse("1234", "0", "namespce",
                new String[] { "key" }, new String[] { "value" },
                new BigDecimal("2000.00"), new BigDecimal("2000.00"),
                new BigDecimal("2000.00"));
    }

    @Override
    public StandardMoneyResponse addMoney(
            AddMoneyRequest addMoneyRequest) throws EwalletException {
        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moneyResponse.setResultCode("0");
        return moneyResponse;
    }

    @Override
    public StandardMoneyResponse transfer(
            TransferRequest transferRequest) throws EwalletException {
        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moneyResponse.setTransactionId("123456789");
        moneyResponse.setResultCode("0");
        return moneyResponse;
    }

    @Override
    public VerifyTransferResponse verifyTransfer(
            VerifyTransferRequest verifyTransferRequest)
            throws EwalletException {
        return new VerifyTransferResponse("1234", "0", "namespce",
                new String[] { "key" }, new String[] { "value" },
                "stub@local.com", new BigDecimal(100.00), "Target Fullname");
    }

}
