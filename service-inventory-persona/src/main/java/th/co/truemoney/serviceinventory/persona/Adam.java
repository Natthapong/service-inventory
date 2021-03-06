package th.co.truemoney.serviceinventory.persona;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.DeleteFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritedRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;

public class Adam implements Persona {

    BigDecimal balance = new BigDecimal(10000);

    @Override
    public TmnProfileProxy getTmnProfile() {
        return new TmnProfileProxy() {

            @Override
            public GetBasicProfileResponse getBasicProfile(
                    StandardBizRequest standardBizRequest)
                    throws EwalletException {
                return new GetBasicProfileResponse("1", "0", "namespace",
                        new String[] { "key" }, new String[] { "value" },
                        "adam", "adam@tmn.com", "0891111111",
                        balance, "C", 3);
            }

            @Override
            public CreateTmnProfileResponse createTmnProfile(
                    CreateTmnProfileRequest createTmnProfileRequest)
                    throws EwalletException {
                return new CreateTmnProfileResponse("1", "0", "namespace",
                        new String[] { "key" }, new String[] { "value" },
                        "123123");
            }

            @Override
            public ListSourceResponse listSource(
                    ListSourceRequest listSourceRequest)
                    throws EwalletException {

                SourceContext[] sourceContext = new SourceContext[3];
                sourceContext[0] = new SourceContext("1", "type", new String[] {
                        "SCB", "xxxx1234" });
                sourceContext[1] = new SourceContext("2", "type", new String[] {
                        "KTB", "xxxx5678" });
                sourceContext[2] = new SourceContext("3", "type", new String[] {
                        "BBL", "xxxx9101" });

                return new ListSourceResponse("1", "2", "namespace",
                        new String[] { "key" }, new String[] { "value" },
                        sourceContext);
            }

            @Override
            public StandardBizResponse isFavoritable(IsFavoritableRequest isIsFavoritableRequest)
                    throws EwalletException {
                return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
            }

            @Override
            public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
                    throws EwalletException {
                return new AddFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, new FavoriteContext());
            }

            @Override
            public ListFavoriteResponse listFavorite(ListFavoriteRequest listFavoriteRequest)
                    throws EwalletException {
                FavoriteContext[] favoriteContexts = new FavoriteContext[3];
                favoriteContexts[0] = new FavoriteContext("1", "billpay", "d.trmv", "", new BigDecimal("13.00"), "20000211101010");
                favoriteContexts[1] = new FavoriteContext("2", "billpay", "d.tmvh", "", new BigDecimal("19.00"), "20000212101010");
                favoriteContexts[2] = new FavoriteContext("3", "billpay", "d.tlp", "", new BigDecimal("18.00"), "20000210101010");

                return new ListFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContexts);
            }

            @Override
            public StandardBizResponse isFavorited(
                    IsFavoritedRequest isFavoritedRequest)
                    throws EwalletException {
                return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
            }

            @Override
            public StandardBizResponse removeFavorite(
                    DeleteFavoriteRequest removeFavoriteRequest)
                    throws EwalletException {
                return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
            }
        };
    }

    @Override
    public TmnSecurityProxy getTmnSecurity() {
        return new TmnSecurityProxy() {

            @Override
            public SignonResponse signon(SignonRequest signOnRequest)
                    throws EwalletException {

                String initiator = signOnRequest.getInitiator();
                String password = signOnRequest.getPin();

                if ("adam@tmn.com".equals(initiator)
                        && "password".equals(password)) {

                    return new SignonResponse("1", "0", "namespace",
                            new String[] { "key" }, new String[] { "value" },
                            "sessionId", "AdamTmnMoneyId");
                }

                throw new FailResultCodeException("4", "");
            }

            @Override
            public CreateSessionResponse createSession()
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AuthenticateResponse authenticate(
                    AuthenticateRequest authenticateRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StandardBizResponse extendSession(
                    StandardBizRequest standardBizRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StandardBizResponse terminateSession(
                    StandardBizRequest standardBizRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
