package th.co.truemoney.serviceinventory.persona.proxies;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

import com.tmn.core.api.message.AddMoneyRequest;
import com.tmn.core.api.message.GetBalanceResponse;
import com.tmn.core.api.message.StandardBizRequest;
import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.TransferRequest;
import com.tmn.core.api.message.VerifyAddMoneyRequest;
import com.tmn.core.api.message.VerifyTransferRequest;
import com.tmn.core.api.message.VerifyTransferResponse;

public class LocalWalletProxyClient implements WalletProxyClient {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";
    
	@Override
	public VerifyTransferResponse verifyTransfer(VerifyTransferRequest verifyTransferRequest)
			throws EwalletException {
        if (verifyTransferRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getWalletProxyClient().verifyTransfer(verifyTransferRequest);
        } else if (verifyTransferRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getWalletProxyClient().verifyTransfer(verifyTransferRequest);
        } else if (verifyTransferRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getWalletProxyClient().verifyTransfer(verifyTransferRequest);
        } else {
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
	}

	@Override
	public StandardMoneyResponse transfer(TransferRequest transferRequest)
			throws EwalletException {
        if (transferRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getWalletProxyClient().transfer(transferRequest);
        } else if (transferRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getWalletProxyClient().transfer(transferRequest);
        } else if (transferRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getWalletProxyClient().transfer(transferRequest);
        } else {
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

	@Override
	public GetBalanceResponse getBalance(StandardBizRequest standardBizRequest)
			throws EwalletException {
        if (standardBizRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getWalletProxyClient().getBalance(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getWalletProxyClient().getBalance(standardBizRequest);
        } else if (standardBizRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getWalletProxyClient().getBalance(standardBizRequest);
        } else {
			GetBalanceResponse balanceResponse = new GetBalanceResponse();
			balanceResponse.setTransactionId("123456789");
			balanceResponse.setResultCode("0");
			balanceResponse.setResultNamespace("core");
			balanceResponse.setAvailableBalance(BigDecimal.TEN);
			balanceResponse.setCurrentBalance(BigDecimal.TEN);
	        return balanceResponse;
        }
	}

	@Override
	public StandardMoneyResponse verifyAddMoney(
			VerifyAddMoneyRequest verifyAddMoneyRequest)
			throws EwalletException {
        if (verifyAddMoneyRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getWalletProxyClient().verifyAddMoney(verifyAddMoneyRequest);
        } else if (verifyAddMoneyRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getWalletProxyClient().verifyAddMoney(verifyAddMoneyRequest);
        } else if (verifyAddMoneyRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getWalletProxyClient().verifyAddMoney(verifyAddMoneyRequest);
        } else {
	        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
	        moneyResponse.setTransactionId("123456789");
	        moneyResponse.setResultCode("0");
	        moneyResponse.setResultNamespace("core");
	        return moneyResponse;
        }
	}

	@Override
	public StandardMoneyResponse addMoney(AddMoneyRequest addMoneyRequest)
			throws EwalletException {
        if (addMoneyRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getWalletProxyClient().addMoney(addMoneyRequest);
        } else if (addMoneyRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getWalletProxyClient().addMoney(addMoneyRequest);
        } else if (addMoneyRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getWalletProxyClient().addMoney(addMoneyRequest);
        } else {
	        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
	        moneyResponse.setTransactionId("123456789");
	        moneyResponse.setResultCode("0");
	        moneyResponse.setResultNamespace("core");
	        return moneyResponse;
        }
	}

}
