package th.co.truemoney.serviceinventory.persona;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;

import com.tmn.core.api.message.AddFavoriteRequest;
import com.tmn.core.api.message.AddFavoriteResponse;
import com.tmn.core.api.message.AddMoneyRequest;
import com.tmn.core.api.message.ChangePasswordRequest;
import com.tmn.core.api.message.ChangePinRequest;
import com.tmn.core.api.message.CreateTmnProfileRequest;
import com.tmn.core.api.message.CreateTmnProfileResponse;
import com.tmn.core.api.message.DeleteFavoriteRequest;
import com.tmn.core.api.message.FavoriteContext;
import com.tmn.core.api.message.GetBalanceResponse;
import com.tmn.core.api.message.GetBasicProfileResponse;
import com.tmn.core.api.message.GetProfileRequest;
import com.tmn.core.api.message.GetProfileResponse;
import com.tmn.core.api.message.IsFavoritableRequest;
import com.tmn.core.api.message.IsFavoritedRequest;
import com.tmn.core.api.message.ListFavoriteRequest;
import com.tmn.core.api.message.ListFavoriteResponse;
import com.tmn.core.api.message.ListSourceRequest;
import com.tmn.core.api.message.ListSourceResponse;
import com.tmn.core.api.message.SignonRequest;
import com.tmn.core.api.message.SignonResponse;
import com.tmn.core.api.message.SourceContext;
import com.tmn.core.api.message.StandardBizRequest;
import com.tmn.core.api.message.StandardBizResponse;
import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.TransferRequest;
import com.tmn.core.api.message.UpdateProfileRequest;
import com.tmn.core.api.message.VerifyAddMoneyRequest;
import com.tmn.core.api.message.VerifyTransferRequest;
import com.tmn.core.api.message.VerifyTransferResponse;

public class Simpsons implements Persona {

    private BigDecimal balance = new BigDecimal(1000);

    @Override
    public TmnSecurityProxyClient getTmnSecurityClient() {
        return new TmnSecurityProxyClient() {

      		@Override
			public StandardBizResponse terminateSession(StandardBizRequest standardBizRequest)
					throws EwalletException {
				StandardBizResponse standardBizResponse = new StandardBizResponse();
				standardBizResponse.setResultCode("0");	
				standardBizResponse.setResultNamespace("core");	
				return standardBizResponse;
			}
			
			@Override
			public SignonResponse signon(SignonRequest signOnRequest)
					throws EwalletException {
			 	String initiator = signOnRequest.getInitiator();
                String password = signOnRequest.getPin();
                if ("simpson@tmn.com".equals(initiator) && "password".equals(password)) {	                	
                	SignonResponse signOnResponse = new SignonResponse();
                	signOnResponse.setTransactionId("1");
                	signOnResponse.setResultCode("0");
                	signOnResponse.setResultNamespace("core");
                	signOnResponse.setDetailKey(new String[] { "key" });
                	signOnResponse.setDetailValue(new String[] { "value" });
                	signOnResponse.setSessionId("sessionId");
                	signOnResponse.setTmnId("SimpsonsTmnMoneyId");
                	return signOnResponse;
                }
                throw new FailResultCodeException("4", "");
			}
			
			@Override
			public StandardBizResponse extendSession(StandardBizRequest standardBizRequest)
					throws EwalletException {
				StandardBizResponse standardBizResponse = new StandardBizResponse();
				standardBizResponse.setResultCode("0");	
				standardBizResponse.setResultNamespace("core");	
				return standardBizResponse;
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

	@Override
	public TmnProfileProxyClient getTmnProfileClient() {
		return new TmnProfileProxyClient() {
			
			@Override
			public StandardBizResponse updateProfile(UpdateProfileRequest updateProfileRequest) throws EwalletException {
				StandardBizResponse standardBizResponse = new StandardBizResponse();
				standardBizResponse.setResultCode("0");	
				standardBizResponse.setResultNamespace("core");	
				return standardBizResponse;
			}
			
			@Override
			public GetProfileResponse getProfile(GetProfileRequest getProfileRequest)
					throws EwalletException {
	        	GetProfileResponse profileResponse = new GetProfileResponse();
	        	profileResponse.setTransactionId("1");
	        	profileResponse.setResultCode("0");
	        	profileResponse.setResultNamespace("core");
	        	profileResponse.setFullName("eve");
	        	profileResponse.setMobile("0891231234");
	        	profileResponse.setEmail("simpsons@tmn.com");
				return profileResponse;
			}
			
			@Override
			public StandardBizResponse changePin(ChangePinRequest changePinRequest) throws EwalletException {
				StandardBizResponse standardBizResponse = new StandardBizResponse();
				standardBizResponse.setResultCode("0");	
				standardBizResponse.setResultNamespace("core");	
				return standardBizResponse;
			}
			
			@Override
			public StandardBizResponse changePassword(ChangePasswordRequest changePasswordRequest)
					throws EwalletException {
				StandardBizResponse standardBizResponse = new StandardBizResponse();
				standardBizResponse.setResultCode("0");	
				standardBizResponse.setResultNamespace("core");	
				return standardBizResponse;
			}

			@Override
			public CreateTmnProfileResponse createTmnProfile(CreateTmnProfileRequest createTmnProfileRequest)
					throws EwalletException {
        		CreateTmnProfileResponse response = new CreateTmnProfileResponse();
            	response.setTransactionId("1");
            	response.setResultCode("0");
            	response.setResultNamespace("namespace");
            	response.setTmnId("123123");
        		return response;
			}

			@Override
			public GetBasicProfileResponse getBasicProfile(StandardBizRequest standardBizRequest)
					throws EwalletException {
			   GetBasicProfileResponse basicProfileResponse = new GetBasicProfileResponse();
			   basicProfileResponse.setResultCode("0");
			   basicProfileResponse.setResultNamespace("namespace");
			   basicProfileResponse.setTransactionId("1");
			   basicProfileResponse.setEmail("simpson@tmn.com");
			   basicProfileResponse.setMobile("0893333333");
			   basicProfileResponse.setFullName("simpson");
			   basicProfileResponse.setEwalletBalance(BigDecimal.TEN);
			   basicProfileResponse.setProfileType("C");
			   basicProfileResponse.setStatusId(3);
			   return basicProfileResponse;
			}

            @Override
            public ListSourceResponse listSource(ListSourceRequest listSourceRequest)
                    throws EwalletException {
                SourceContext[] sourceContext = createSourceContexts();
                ListSourceResponse listSourceResponse = new ListSourceResponse();
                listSourceResponse.setTransactionId("1");
                listSourceResponse.setResultCode("0");
                listSourceResponse.setResultNamespace("namespace");
                listSourceResponse.setSourceList(sourceContext);
                return listSourceResponse;
            }
            
        	private SourceContext[] createSourceContexts() {
        		SourceContext[] sourceContext = new SourceContext[3];
        		sourceContext[0] = createSourceContext("1", "type", new String[] { "SCB", "xxxx1234" });
        		sourceContext[1] = createSourceContext("2", "type", new String[] { "KTB", "xxxx5678" });
                sourceContext[2] = createSourceContext("3", "type", new String[] { "BBL", "xxxx9101" });
        		return sourceContext;
        	}
        	
        	private SourceContext createSourceContext(String sourceId, String sourceType, String[] sourceDetail) {
        		SourceContext sourceContext = new SourceContext();
        		sourceContext.setSourceId(sourceId);
        		sourceContext.setSourceType(sourceType);
        		sourceContext.setSourceDetail(sourceDetail);
        		return sourceContext;
        	}

            @Override
            public StandardBizResponse isFavoritable(IsFavoritableRequest isIsFavoritableRequest)
                    throws EwalletException {
    			StandardBizResponse standardBizResponse = new StandardBizResponse();
    			standardBizResponse.setTransactionId("1");
    			standardBizResponse.setResultCode("0");	
    			standardBizResponse.setResultNamespace("namespace");	
    			return standardBizResponse;
            }

			@Override
			public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
					throws EwalletException {
            	FavoriteContext favoriteContext = createFavoriteContext("1003", "billpay", "tcg", new BigDecimal("15.00"), "20000211101013");
                AddFavoriteResponse response = new AddFavoriteResponse();
                response.setTransactionId("1");
    			response.setResultCode("0");	
    			response.setResultNamespace("namespace");	
    			response.setFavorite(favoriteContext);
    			return response;
			}
			
        	private FavoriteContext[] createFavoriteContexts() {
        		FavoriteContext[] favoriteContexts = new FavoriteContext[4];
        		favoriteContexts[0] = createFavoriteContext("1", "billpay", "trmv", new BigDecimal("13.00"), "20000211101010");
                favoriteContexts[1] = createFavoriteContext("2", "billpay", "tmvh", new BigDecimal("19.00"), "20000212101010");
                favoriteContexts[2] = createFavoriteContext("3", "billpay", "tlp",  new BigDecimal("18.00"), "20000210101010");
                favoriteContexts[3] = createFavoriteContext("3", "billpay", "mea",  new BigDecimal("20.00"), "20000210101010");
        		return favoriteContexts;
        	}

        	private FavoriteContext createFavoriteContext(String favoriteId, String serviceType, String serviceCode, BigDecimal amount, String reference1) {
        		FavoriteContext favoriteContext = new FavoriteContext();
        		favoriteContext.setFavoriteId(favoriteId);
        		favoriteContext.setServiceType(serviceType);
        		favoriteContext.setServiceCode(serviceCode);
        		favoriteContext.setAmount(amount);
        		favoriteContext.setReference1(reference1);
        		return favoriteContext;
        	}

            @Override
            public StandardBizResponse removeFavorite(DeleteFavoriteRequest removeFavoriteRequest)
                    throws EwalletException {
    			StandardBizResponse standardBizResponse = new StandardBizResponse();
    			standardBizResponse.setTransactionId("1");
    			standardBizResponse.setResultCode("0");	
    			standardBizResponse.setResultNamespace("namespace");	
    			return standardBizResponse;
            }

            @Override
            public ListFavoriteResponse listFavorite(ListFavoriteRequest listFavoriteRequest)
                    throws EwalletException {
                FavoriteContext[] favoriteContexts = createFavoriteContexts();
                ListFavoriteResponse listFavoriteResponse = new ListFavoriteResponse();
                listFavoriteResponse.setTransactionId("1");
                listFavoriteResponse.setResultCode("0");	
                listFavoriteResponse.setResultNamespace("namespace");	
                listFavoriteResponse.setFavoriteList(favoriteContexts);
        		return listFavoriteResponse;
            }

            @Override
            public StandardBizResponse isFavorited(IsFavoritedRequest isFavoritedRequest)
                    throws EwalletException {
            	throw new FailResultCodeException("2014", "stub ewallet client");
            }
            
		};
	}
	
	@Override
	public WalletProxyClient getWalletProxyClient() {
		return new WalletProxyClient() {
			
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
		        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		        moneyResponse.setTransactionId("123456789");
		        moneyResponse.setResultCode("0");
		        moneyResponse.setResultNamespace("core");
		        return moneyResponse;
			}
			
			@Override
			public GetBalanceResponse getBalance(StandardBizRequest standardBizRequest)
					throws EwalletException {
				GetBalanceResponse balanceResponse = new GetBalanceResponse();
				balanceResponse.setTransactionId("123456789");
				balanceResponse.setResultCode("0");
				balanceResponse.setResultNamespace("core");
				balanceResponse.setAvailableBalance(BigDecimal.TEN);
				balanceResponse.setCurrentBalance(BigDecimal.TEN);
		        return balanceResponse;
			}

			@Override
			public StandardMoneyResponse verifyAddMoney(VerifyAddMoneyRequest verifyAddMoneyRequest)
					throws EwalletException {
		        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		        moneyResponse.setTransactionId("123456789");
		        moneyResponse.setResultCode("0");
		        moneyResponse.setResultNamespace("core");
		        return moneyResponse;
			}

			@Override
			public StandardMoneyResponse addMoney(AddMoneyRequest addMoneyRequest) throws EwalletException {
		        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
		        moneyResponse.setTransactionId("123456789");
		        moneyResponse.setResultCode("0");
		        moneyResponse.setResultNamespace("core");
		        return moneyResponse;
			}
			
		};
	}
	
}
