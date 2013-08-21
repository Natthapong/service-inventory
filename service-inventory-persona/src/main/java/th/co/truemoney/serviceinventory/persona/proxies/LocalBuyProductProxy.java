package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BuyProxy;

public class LocalBuyProductProxy implements BuyProxy {
	
    @Override
	public VerifyBuyResponse verifyBuyProduct(VerifyBuyRequest request)
			throws SIEngineException {
    	
        SIEngineResponse response = new SIEngineResponse();
        response.setResultNamespace("core");
        response.setResultCode("0");
        response.setResultDesc("SUCCESS");
        response.setReqTransactionID("3310R0041");
        response.setTransactionID("130419013811");
        response.setResponseMessage("SUCCESS");

        response.addParameterElement("amount", "30000");
        response.addParameterElement("trans_relation", "20130603");
        response.addParameterElement("source", "EW");

        return new VerifyBuyResponse(response);
	}

	@Override
	public ConfirmBuyResponse confirmBuyProduct(ConfirmBuyRequest request)
			throws SIEngineException {
		// TODO Auto-generated method stub
		return null;
	}

}