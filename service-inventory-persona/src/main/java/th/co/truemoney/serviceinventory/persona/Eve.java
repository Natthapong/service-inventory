package th.co.truemoney.serviceinventory.persona;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteResponse;
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
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;

public class Eve implements Persona {

    private BigDecimal balance = new BigDecimal(20000);

    @Override
    public TmnProfileProxy getTmnProfile() {
        return new TmnProfileProxy() {

            @Override
            public GetBasicProfileResponse getBasicProfile(
                    StandardBizRequest standardBizRequest)
                    throws EwalletException {
                return new GetBasicProfileResponse("1", "0", "namespace",
                        new String[] { "key" }, new String[] { "value" },
                        "eve", "eve@tmn.com", "0892222222",
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
                //return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
                throw new FailResultCodeException("2013", "stub ewallet client");
            }

            @Override
            public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
                    throws EwalletException {
                return new AddFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, new FavoriteContext());
            }

            @Override
            public ListFavoriteResponse listFavorite(ListFavoriteRequest listFavoriteRequest)
                    throws EwalletException {
                FavoriteContext[] favoriteContexts = new FavoriteContext[1];
                favoriteContexts[0] = new FavoriteContext("1", "billpay", "d.trmv", "", new BigDecimal("13.00"), "20000211101010");

                return new ListFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContexts);
            }

            @Override
            public StandardBizResponse isFavorited(
                    IsFavoritedRequest isFavoritedRequest)
                    throws EwalletException {
                throw new FailResultCodeException("2014", "stub ewallet client");
            }

            @Override
            public StandardBizResponse removeFavorite(
                    DeleteFavoriteRequest removeFavoriteRequest)
                    throws EwalletException {

                throw new FailResultCodeException("xxx", "Favorite delete fail.");

            }

        };
    }

    @Override
    public TmnSecurityProxyClient getTmnSecurity() {
        return new TmnSecurityProxyClient() {

      		@Override
			public com.tmn.core.api.message.StandardBizResponse terminateSession(
					com.tmn.core.api.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public com.tmn.core.api.message.SignonResponse signon(com.tmn.core.api.message.SignonRequest signOnRequest)
					throws EwalletException {
			 	String initiator = signOnRequest.getInitiator();
                String password = signOnRequest.getPin();

                if ("eve@tmn.com".equals(initiator) && "password".equals(password)) {	                	
                	com.tmn.core.api.message.SignonResponse signOnResponse = new com.tmn.core.api.message.SignonResponse();
                	signOnResponse.setTransactionId("1");
                	signOnResponse.setResultCode("0");
                	signOnResponse.setResultNamespace("core");
                	signOnResponse.setDetailKey(new String[] { "key" });
                	signOnResponse.setDetailValue(new String[] { "value" });
                	signOnResponse.setSessionId("sessionId");
                	signOnResponse.setTmnId("EveTmnMoneyId");
                	return signOnResponse;
                }
                throw new FailResultCodeException("4", "");
			}
			
			@Override
			public com.tmn.core.api.message.StandardBizResponse extendSession(
					com.tmn.core.api.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
			}			

        };
    }

    @Override
    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    };

}
