package th.co.truemoney.serviceinventory.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxyImpl;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxyImpl;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritableRequest;
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
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {
	
	@Bean @Qualifier("endpoint.host")
	public String host() {
		return "http://127.0.0.1:8585";
	}
	
//	@Bean
//	@Primary
//	public ActivityService stubActivityService(){
//		return new ActivityService() {
//			
//			@Override
//			public ActivityDetail getActivityDetail(Long reportID,
//					String accessTokenID) throws ServiceInventoryException {
//				ActivityDetail activityDetail = new ActivityDetail();
//				activityDetail.setAmount(new BigDecimal(1000));
//				return activityDetail;
//			}
//			
//			@Override
//			public List<Activity> getActivities(String accessTokenID)
//					throws ServiceInventoryException {
//				List<Activity> activities = new ArrayList<Activity>();
//				Activity activity = new Activity();
//				activity.setReportID(new Long(999));
//				activities.add(activity);
//				return activities;
//			}
//		};
//	}
	
	Adam adam = new Adam();
	Eve eve = new Eve();

	@Bean
	@Primary
	public TmnProfileProxy stubTmnProfileProxy() {

		return new TmnProfileProxy() {

			@Override
			public GetBasicProfileResponse getBasicProfile(
					StandardBizRequest standardBizRequest)
					throws EwalletException {
				if(standardBizRequest.getSecurityContext().getTmnId().equals("AdamTmnMoneyId")){
					return adam.getTmnProfile().getBasicProfile(standardBizRequest);
				}else if(standardBizRequest.getSecurityContext().getTmnId().equals("EveTmnMoneyId")){
					return eve.getTmnProfile().getBasicProfile(standardBizRequest);
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
				return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
			}

			@Override
			public StandardBizResponse addFavorite(AddFavoriteRequest addFavoriteRequest)
					throws EwalletException {
				return new StandardBizResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" });
			}
			
			@Override
			public ListFavoriteResponse listFavorite(ListFavoriteRequest listFavoriteRequest)
					throws EwalletException {
				FavoriteContext[] favoriteContexts = new FavoriteContext[3];
				favoriteContexts[0] = new FavoriteContext("1", "", "", "", new BigDecimal("1.00"), "");
				favoriteContexts[0] = new FavoriteContext("2", "", "", "", new BigDecimal("1.00"), "");
				favoriteContexts[0] = new FavoriteContext("3", "", "", "", new BigDecimal("1.00"), "");

				return new ListFavoriteResponse("1", "0", "namespace", new String[] { "key" }, new String[] { "value" }, favoriteContexts);
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
			public StandardBizResponse isCreatable(
					IsCreatableRequest isCreatableRequest)
					throws EwalletException {
				if(isCreatableRequest != null && "local@tmn.com".equals(isCreatableRequest.getLoginId())){
					throw new FailResultCodeException("401", "EWALLET-PROXY");
				}else{
					return new StandardBizResponse("1", "0", "EWALLET-PROXY",
						new String[] { "email" }, new String[] { isCreatableRequest.getLoginId() });
				}
			}

			@Override
			public StandardBizResponse updateAccount(
					UpdateAccountRequest updateAccountRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

	@Bean
	public OTPGenerator otpGenerator() {
		return new UnSecureOTPGenerator();
	}

	@Bean
	public JavaMailSender stubJavaMailSender() {
		return new JavaMailSender() {

			@Override
			public void send(SimpleMailMessage simpleMessage)
					throws MailException {
				try {
					System.out.println("stubJavaMailSender.send start");
					Thread.sleep(3000);
					System.out.println("stubJavaMailSender.send stop");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void send(SimpleMailMessage[] simpleMessages)
					throws MailException {
				// TODO Auto-generated method stub
			}

			@Override
			public MimeMessage createMimeMessage() {
				return new MimeMessage(Session.getInstance(new Properties()));
			}

			@Override
			public MimeMessage createMimeMessage(InputStream contentStream)
					throws MailException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void send(MimeMessage mimeMessage) throws MailException {
				try {
					System.out.println("stubJavaMailSender.send start");
					Thread.sleep(3000);
					System.out.println("stubJavaMailSender.send stop");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void send(MimeMessage[] mimeMessages) throws MailException {
			}

			@Override
			public void send(MimeMessagePreparator mimeMessagePreparator)
					throws MailException {
				// TODO Auto-generated method stub
			}

			@Override
			public void send(MimeMessagePreparator[] mimeMessagePreparators)
					throws MailException {
				// TODO Auto-generated method stub
			}
		};
	}

	@Bean
	public FreeMarkerConfigurationFactory stubFreeMarkerConfigurationFactory() {
		FreeMarkerConfigurationFactory freeMarkerConfigurationFactory = new FreeMarkerConfigurationFactory();
		freeMarkerConfigurationFactory.setTemplateLoaderPath("email-template");
		return freeMarkerConfigurationFactory;
	}

	@Bean @Qualifier("emailEncoding")
	public String getEmailEncoding() {
		return "utf-8";
	}

	@Bean @Qualifier("emailSender")
	public String getEmailSender() {
		return "emailSender";
	}

	@Bean @Qualifier("welcomeSubject")
	public String getWelcomeSubject() {
		return "welcomeSubject";
	}

	@Bean @Qualifier("welcomeTemplate")
	public String getWelcomeTemplate() {
		return "welcome-email.ftl";
	}

	@Bean
	public BillProxy billPayProxy() {
		return new BillProxyImpl();
	}
	
	@Bean
	public TopUpMobileProxy topUpMobileProxy() {
		return new TopUpMobileProxyImpl();
	}

	@Bean @Qualifier("barcodeInfoURL") @Primary
	public String barcodeInfoURL() {
		return "http://localhost:8585/service-inventory-web/v1/test/postGetBill";
	}

	@Bean @Qualifier("verifyBillPayURL") @Primary
	public String verifyBillPayURL() {
		return "http://localhost:8585/service-inventory-web/v1/test/postVerifyBill";
	}

	@Bean @Qualifier("confirmBillPayURL") @Primary
	public String confirmBillPayURL() {
		return "http://localhost:8585/service-inventory-web/v1/test/postConfirmBill";
	}
	
	@Bean @Qualifier("verifyTopUpMobileURL") @Primary
	public String verifyTopUpMobileURL() {
		return "http://localhost:8585/service-inventory-web/v1/test/postVerifyTopup";
	}
	
	@Bean @Qualifier("confirmTopUpMobileURL") @Primary
	public String confirmTopUpMobileURL() {
		return "http://localhost:8585/service-inventory-web/v1/test/postConfirmTopup";
	}
	
}