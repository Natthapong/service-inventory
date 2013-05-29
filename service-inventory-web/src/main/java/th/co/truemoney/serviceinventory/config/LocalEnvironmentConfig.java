package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.DeleteFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
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
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.TransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.UpdateAccountRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.impl.SmsProxyImpl;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;
import th.co.truemoney.serviceinventory.persona.TrueConvergenceOneBillPersona;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {

    private static Logger logger = LoggerFactory.getLogger(LocalEnvironmentConfig.class);

    @Bean @Qualifier("endpoint.host") @Primary
    public String host() {
        return "http://127.0.0.1:8787";
    }

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";

    @Bean
    @Primary
    public TmnProfileProxy stubTmnProfileProxy() {

        return new TmnProfileProxy() {

            @Override
            public GetBasicProfileResponse getBasicProfile(
                    StandardBizRequest standardBizRequest)
                    throws EwalletException {
                if(standardBizRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)){
                    return adam.getTmnProfile().getBasicProfile(standardBizRequest);
                }else if(standardBizRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)){
                    return eve.getTmnProfile().getBasicProfile(standardBizRequest);
                }else if(standardBizRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)){
                    return simpsons.getTmnProfile().getBasicProfile(standardBizRequest);
                }else{
                    return new GetBasicProfileResponse("1", "0", "namespace",
                            new String[] { "key" }, new String[] { "value" },
                            "username", "local@tmn.com", "0891231234",
                            new BigDecimal(50.0d), "C", 3);
                }
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
                List<String> serviceCodes = Arrays.asList("tr", "trmv", "tmvh", "tlp", "tic", "ti", "tcg");
                if(!serviceCodes.contains(isIsFavoritableRequest.getServiceCode())) {
                    throw new FailResultCodeException("2013", "stub ADD_FAVORITE_DENIED");
                }
                if(isIsFavoritableRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)){
                    return adam.getTmnProfile().isFavoritable(isIsFavoritableRequest);
                }else if(isIsFavoritableRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)){
                    return eve.getTmnProfile().isFavoritable(isIsFavoritableRequest);
                }else if(isIsFavoritableRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)){
                    return simpsons.getTmnProfile().isFavoritable(isIsFavoritableRequest);
                }else {
                    return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
                }
            }

            @Override
            public AddFavoriteResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
                    throws EwalletException {
                logger.debug("TMN ID: " + addFavoriteRequest.getSecurityContext().getTmnId());
                FavoriteContext favoriteContext = new FavoriteContext();
                if(addFavoriteRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)){
                    favoriteContext.setFavoriteId("1001");
                    return new AddFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContext);
                }else if(addFavoriteRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)){
                    favoriteContext.setFavoriteId("1002");
                    return new AddFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContext);
                }else if(addFavoriteRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)){
                    favoriteContext.setFavoriteId("1003");
                    return new AddFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContext);
                }else{
                    throw new FailResultCodeException("500", "stub namespace.");
                }
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
                if(isFavoritedRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
                    return adam.getTmnProfile().isFavorited(isFavoritedRequest);
                } else if(isFavoritedRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
                    return eve.getTmnProfile().isFavorited(isFavoritedRequest);
                } else if(isFavoritedRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
                    return simpsons.getTmnProfile().isFavorited(isFavoritedRequest);
                }else {
                    return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
                }
            }

			@Override
			public StandardBizResponse removeFavorite(
					DeleteFavoriteRequest removeFavoriteRequest)
					throws EwalletException {
                if(removeFavoriteRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
                    return adam.getTmnProfile().removeFavorite(removeFavoriteRequest);
                } else if(removeFavoriteRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
                    return eve.getTmnProfile().removeFavorite(removeFavoriteRequest);
                } else if(removeFavoriteRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
                    return simpsons.getTmnProfile().removeFavorite(removeFavoriteRequest);
                }else {
                    return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
                }
			}

        };
    }

    @Bean
    @Primary
    public TmnSecurityProxy stubTmnSecurityProxy() {
        return new TmnSecurityProxy() {

            @Override
            public StandardBizResponse terminateSession(
                    th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
                    throws EwalletException {
                return new StandardBizResponse("1", "0", "namespace",
                        new String[] { "key" }, new String[] { "value" });
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

                }else if("local@tmn.com".equals(initiator)
                        && "password".equals(password)){
                    return new SignonResponse("1", "0", "namespace",
                            new String[] { "key" }, new String[] { "value" },
                            "sessionId", "1000");

                }

                throw new SignonServiceException("4", "");
            }

            @Override
            public StandardBizResponse extendSession(
                    th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
                    throws EwalletException {
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
        };
    }

    @Bean
    @Primary
    public EwalletSoapProxy stubEWalletSoapProxy() {
        return new EwalletSoapProxy() {

            @Override
            public StandardMoneyResponse verifyAddMoney(
                    VerifyAddMoneyRequest verifyAddMoneyRequest)
                    throws EwalletException {
                return new StandardMoneyResponse("1234", "0", "namespce",
                        new String[] { "key" }, new String[] { "value" },
                        "stub@local.com", new BigDecimal(100.00));
            }

            @Override
            public GetBalanceResponse getBalance(
                    th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
                    throws EwalletException {
                return new GetBalanceResponse("1234", "0", "namespce",
                        new String[] { "key" }, new String[] { "value" },
                        new BigDecimal("2000.00"), new BigDecimal("2000.00"),
                        new BigDecimal("2000.00"));
            }

            @Override
            public StandardMoneyResponse addMoney(
                    AddMoneyRequest addMoneyRequest) throws EwalletException {
                StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moneyResponse.setResultCode("0");
                return moneyResponse;
            }

            @Override
            public StandardMoneyResponse transfer(
                    TransferRequest transferRequest) throws EwalletException {
                StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moneyResponse.setTransactionId("123456789");
                moneyResponse.setResultCode("0");
                return moneyResponse;
            }

            @Override
            public VerifyTransferResponse verifyTransfer(
                    VerifyTransferRequest verifyTransferRequest)
                    throws EwalletException {
                return new VerifyTransferResponse("1234", "0", "namespce",
                        new String[] { "key" }, new String[] { "value" },
                        "stub@local.com", new BigDecimal(100.00), "Target Fullname");
            }
        };
    }

    @Bean
    @Primary
    public SmsProxy stubSmsProxy() {
        return new SmsProxy() {

            @Override
            public SmsResponse send(SmsRequest request) {
                StringBuilder xmlResponse = new StringBuilder();
                xmlResponse
                        .append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                xmlResponse.append("<message id=\"13619484376108002\">");
                xmlResponse.append("<rsr type=\"ack\">");
                xmlResponse.append("<service-id/>");
                xmlResponse
                        .append("<destination messageid=\"13619484376108002\">");
                xmlResponse.append("<address>");
                xmlResponse
                        .append("<number type=\"international\">66891267026</number>");
                xmlResponse.append("</address>");
                xmlResponse.append("</destination>");
                xmlResponse.append("<source>");
                xmlResponse.append("<address>");
                xmlResponse.append("<number type=\"\">TMN-ID</number>");
                xmlResponse.append("</address>");
                xmlResponse.append("</source>");
                xmlResponse.append("<rsr_detail status=\"success\">");
                xmlResponse.append("<code>000</code>");
                xmlResponse.append("<description>success</description>");
                xmlResponse.append("</rsr_detail>");
                xmlResponse.append("</rsr>");
                xmlResponse.append("</message>");

                return new SmsProxyImpl().readXMLResponse(xmlResponse
                        .toString());
            }
        };
    }

    @Bean
    public TmnProfileAdminProxy stubTmnProfileAdminProxy() {
        return new TmnProfileAdminProxy() {

            @Override
            public VerifyForgotPasswordResponse verifyForgotPassword(
                    VerifyForgotPasswordRequest verifyForgotPasswordRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StandardBizResponse confirmForgotPassword(
                    ConfirmForgotPasswordRequest confirmForgotPasswordRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CreateForgotPasswordResponse createForgotPassword(
                    CreateForgotPasswordRequest createForgotPasswordRequest)
                    throws EwalletException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public StandardBizResponse isCreatable(IsCreatableRequest isCreatableRequest) throws EwalletException {
                if(isCreatableRequest != null) {
                    if ("local@tmn.com".equals(isCreatableRequest.getLoginId())) {
                        throw new FailResultCodeException("401", "EWALLET-PROXY");
                    } else {
                        return new StandardBizResponse("1", "0", "EWALLET-PROXY", new String[] { "email" }, new String[] { isCreatableRequest.getLoginId() });
                    }

                }else{
                    throw new FailResultCodeException("401", "EWALLET-PROXY");
                }
            }

            @Override
            public StandardBizResponse updateAccount(UpdateAccountRequest updateAccountRequest)
                    throws EwalletException {
                return null;
            }

        };
    }

    @Bean
    public OTPGenerator otpGenerator() {
        return new UnSecureOTPGenerator();
    }

    @Bean
    public BillProxy billPayProxy() {
        return new TrueConvergenceOneBillPersona().getBillPayProxy();
    }

    @Bean
    public TopUpMobileProxy topUpMobileProxy() {
        return new TrueConvergenceOneBillPersona().getTopUpMobileProxy();
    }
}