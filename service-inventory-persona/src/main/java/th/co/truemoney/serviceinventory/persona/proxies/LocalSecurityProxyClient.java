package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

import com.tmn.core.api.message.SignonRequest;
import com.tmn.core.api.message.SignonResponse;
import com.tmn.core.api.message.StandardBizRequest;
import com.tmn.core.api.message.StandardBizResponse;

public class LocalSecurityProxyClient implements TmnSecurityProxyClient {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    @Override
    public StandardBizResponse terminateSession(StandardBizRequest standardBizRequest)
            throws EwalletException {
        if ("adam@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())) {
            return adam.getTmnSecurity().terminateSession(standardBizRequest);
        } else if("eve@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())){
            return eve.getTmnSecurity().terminateSession(standardBizRequest);
        } else if("simpson@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())){
            return simpsons.getTmnSecurity().terminateSession(standardBizRequest);
        } else {
	    	StandardBizResponse standardBizResponse = new StandardBizResponse();
	    	standardBizResponse.setTransactionId("11111111");
	    	standardBizResponse.setResultCode("0");
	    	standardBizResponse.setResultNamespace("namespace");
	        return standardBizResponse;
        }
    }

    @Override
    public SignonResponse signon(SignonRequest signOnRequest)
            throws EwalletException {

        String initiator = signOnRequest.getInitiator();

        if ("adam@tmn.com".equals(initiator)) {
            return adam.getTmnSecurity().signon(signOnRequest);

        }else if("eve@tmn.com".equals(initiator)){
            return eve.getTmnSecurity().signon(signOnRequest);

        }else if("simpson@tmn.com".equals(initiator)){
            return simpsons.getTmnSecurity().signon(signOnRequest);
        }

        throw new ServiceInventoryException(400, "4", "", "TMN-SERVICE-INVENTORY");
    }

    @Override
    public StandardBizResponse extendSession(StandardBizRequest standardBizRequest) throws EwalletException {
        if ("adam@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())) {
            return adam.getTmnSecurity().extendSession(standardBizRequest);
        } else if("eve@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())){
            return eve.getTmnSecurity().extendSession(standardBizRequest);
        } else if("simpson@tmn.com".equals(standardBizRequest.getSecurityContext().getTmnId())){
            return simpsons.getTmnSecurity().extendSession(standardBizRequest);
        } else {
	    	StandardBizResponse standardBizResponse = new StandardBizResponse();
	    	standardBizResponse.setTransactionId("11111111");
	    	standardBizResponse.setResultCode("0");
	    	standardBizResponse.setResultNamespace("namespace");
	        return standardBizResponse;
        }
    }

}
