package th.co.truemoney.serviceinventory.persona.proxies;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;

import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.TransferRequest;
import com.tmn.core.api.message.VerifyTransferRequest;
import com.tmn.core.api.message.VerifyTransferResponse;

public class LocalWalletProxyClient implements WalletProxyClient {

	@Override
	public VerifyTransferResponse verifyTransfer(VerifyTransferRequest verifyTransferRequest)
			throws EwalletException {
        VerifyTransferResponse verifyTransferResponse = new VerifyTransferResponse();
        verifyTransferResponse.setTransactionId("100000001");
        verifyTransferResponse.setResultCode("0");
        verifyTransferResponse.setResultNamespace("core");
        verifyTransferResponse.setLoginId("stub@local.com");
        verifyTransferResponse.setRemainingBalance(new BigDecimal("100.00"));
        verifyTransferResponse.setTargetFullname("Target Fullname");
        verifyTransferResponse.setTargetProfilePicture("Target Profile Picture");
        return verifyTransferResponse;
	}

	@Override
	public StandardMoneyResponse transfer(TransferRequest transferRequest)
			throws EwalletException {
    	
    	if ("0866666666".equals(transferRequest.getTarget())) {
    		throw new FailResultCodeException("666666", "EW-CORE");
    	}
        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moneyResponse.setTransactionId("123456789");
        moneyResponse.setResultCode("0");
        moneyResponse.setResultNamespace("core");
        return moneyResponse;
	}

}
