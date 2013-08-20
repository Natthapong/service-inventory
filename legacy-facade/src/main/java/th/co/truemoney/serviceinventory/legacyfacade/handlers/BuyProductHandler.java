package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BuyProxy;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.BuyProductBuilder.VerifyBuyProductFailException;

public class BuyProductHandler {

    @Autowired
    private BuyProxy buyProxy;
    
    public BuyProduct verifyBuyProduct(VerifyBuyRequest verifyBuyRequest){
        try {
            VerifyBuyResponse verifyResponse = buyProxy.verifyBuyProduct(verifyBuyRequest);
            BuyProduct buyProduct = new BuyProduct(verifyResponse.getTransactionID(), 
            		verifyResponse.getTransRelation(), 
            		verifyResponse.getAmount());
            return buyProduct;
        } catch(FailResultCodeException ex) {
            throw new VerifyBuyProductFailException(ex);
        }
    }

}
