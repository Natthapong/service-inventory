package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductConfirmationInfo;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BuyProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

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
		} catch (Exception e) {
	        throw new SIEngineUnExpectedException(e);
	    }
    }

	public BuyProductConfirmationInfo confirmBuyProduct(ConfirmBuyRequest confirmBuyRequest) {
		try {
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			ConfirmBuyResponse confirmResponse = buyProxy.confirmBuyProduct(confirmBuyRequest);
 
			BuyProductConfirmationInfo buyProductConfirmationInfo = new BuyProductConfirmationInfo();
			buyProductConfirmationInfo.setTransactionID(confirmResponse.getApproveCode());
			buyProductConfirmationInfo.setTransactionDate(date.format(new Date()));
 
			return buyProductConfirmationInfo;
		} catch(FailResultCodeException ex) {
		    throw new ConfirmBuyProductFailException(ex);
		} catch (Exception e) {
		    throw new SIEngineUnExpectedException(e);
		}
	}

	public void setBuyProxy(BuyProxy buyProxy) {
		this.buyProxy = buyProxy;
	}

    public static class VerifyBuyProductFailException extends ServiceInventoryException{
		private static final long serialVersionUID = 2783748973750123315L;

		public VerifyBuyProductFailException(SIEngineException ex) {
            super(500,ex.getCode(),"Verify Buy product fail with code: " + ex.getCode(),ex.getNamespace(),ex.getMessage());
        }
    }

    public static class ConfirmBuyProductFailException extends ServiceInventoryException{
		private static final long serialVersionUID = -192970131921639753L;

		public ConfirmBuyProductFailException(SIEngineException ex) {
            super(500,ex.getCode(),"Confirm Buy product fail with code: " + ex.getCode(),ex.getNamespace(),ex.getMessage());
        }
    }
    
}
