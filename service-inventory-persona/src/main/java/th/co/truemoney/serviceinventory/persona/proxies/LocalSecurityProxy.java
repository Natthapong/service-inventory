package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

public class LocalSecurityProxy implements TmnSecurityProxy {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";

    @Override
    public StandardBizResponse terminateSession(StandardBizRequest standardBizRequest)
            throws EwalletException {
        return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
    }

    @Override
    public SignonResponse signon(SignonRequest signOnRequest)
            throws EwalletException {

        String initiator = signOnRequest.getInitiator();
        String password = signOnRequest.getPin();

        if ("adam@tmn.com".equals(initiator)
                && "password".equals(password)) {
            return adam.getTmnSecurity().signon(signOnRequest);

        }else if("eve@tmn.com".equals(initiator)
                && "password".equals(password)){
            return eve.getTmnSecurity().signon(signOnRequest);

        }else if("simpson@tmn.com".equals(initiator)
                && "password".equals(password)){
            return new SignonResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    "sessionId", SimpsonsTmnMoneyID);

        }else {
            return new SignonResponse("1", "0", "namespace",
                    new String[] { "key" }, new String[] { "value" },
                    "sessionId", "1000");
        }

        //throw new ServiceInventoryException(400, "4", "", "TMN-SERVICE-INVENTORY");
    }

    @Override
    public StandardBizResponse extendSession(StandardBizRequest standardBizRequest) throws EwalletException {
        return new StandardBizResponse("1", "0", "namespace",
                new String[] { "key" }, new String[] { "value" });
    }

    @Override
    public CreateSessionResponse createSession()
            throws EwalletException {
        return new CreateSessionResponse("0", "namespace", "sessionId");
    }

    @Override
    public AuthenticateResponse authenticate(
            AuthenticateRequest authenticateRequest)
            throws EwalletException {
        return new AuthenticateResponse("1", "0", "namespace",
                new String[] { "key" }, new String[] { "value" },
                "trueMoneyId");
    }

}
