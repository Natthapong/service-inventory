package th.co.truemoney.serviceinventory.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillParameter;
import th.co.truemoney.serviceinventory.bill.domain.BillRequest;
import th.co.truemoney.serviceinventory.bill.domain.BillResponse;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFundFee;
import th.co.truemoney.serviceinventory.bill.exception.BillException;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.bill.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
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
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
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
							"sessionId", "trueMoneyId");

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
	@Primary
	public BillProxy stubBillProxy() {
		return new BillProxy() {
			
			@Override
			public BillResponse verifyBillPay(BillRequest billPayRequest)
					throws BillException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public BillResponse getBarcodeInformation(Integer channelID, String barcode) 
					throws BillException {
						
				BillResponse stubBillResponse = new BillResponse();
				stubBillResponse.setResultCode("0");
				stubBillResponse.setResultDesc("Success");
				stubBillResponse.setResultNamespace("SIENGINE");
				stubBillResponse.setReqTransactionID("4410A0318");
				stubBillResponse.setResponseMessage("Success");
				stubBillResponse.setTransactionID("130401012303");
				
				List<BillParameter> parameters = new ArrayList<BillParameter>();
				BillParameter parameter = new BillParameter();
				parameter.setKey("target");
				parameter.setValue("tcg");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("logo");
				parameter.setValue("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("title_th");
				parameter.setValue("ค่าใช้บริการบริษัทในกลุ่มทรู");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("title_en");
				parameter.setValue("Convergence Postpay");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("ref1_title_th");
				parameter.setValue("โทรศัพท์พื้นฐาน");				
				parameters.add(parameter);
								
				parameter = new BillParameter();
				parameter.setKey("ref1_title_en");
				parameter.setValue("Fix Line");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("ref1");
				parameter.setValue("010004552");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("ref2_title_th");
				parameter.setValue("รหัสลูกค้า");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("ref2_titie_en");
				parameter.setValue("Customer ID");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("ref2");
				parameter.setValue("010520120200015601");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("partial_payment");
				parameter.setValue("Y");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("call_center");
				parameter.setValue("1331");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("amount");
				parameter.setValue("10000");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("service_min_amount");
				parameter.setValue("10000");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("service_max_amount");
				parameter.setValue("3000000");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("service_fee_type");
				parameter.setValue("THB");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("service_fee");
				parameter.setValue("1000");				
				parameters.add(parameter);
				
				parameter = new BillParameter();
				parameter.setKey("total_service_fee");
				parameter.setValue("1000");				
				parameters.add(parameter);
				
				stubBillResponse.setParameters(parameters);
				
				return stubBillResponse;
				
			}
		};
	}
	
	@Bean
    @Primary
    public BillPaymentService stubBillPaymentService() {
            
            return new BillPaymentServiceImpl() {
                    
                    public BillInfo getBillInformation(String barcode, String accessTokenID)
                                    throws ServiceInventoryException {
                            
                            ServiceFee sFee = new ServiceFee();
                            sFee.setFeeType("THB");
                            sFee.setFee(new BigDecimal(10.00));
                            sFee.setTotalFee(new BigDecimal(10.00));
                            
                            SourceOfFundFee sofFee = new SourceOfFundFee();
                            sofFee.setFeeType("THB");
                            sofFee.setFee(new BigDecimal(20.00));
                            sofFee.setTotalFee(new BigDecimal(20.00));
                            
                            SourceOfFundFee[] sofFees = new SourceOfFundFee[]{ sofFee };
                            
                            BillInfo stubInfo = new BillInfo();
                            stubInfo.setTarget("tmvh");
                            stubInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
                            stubInfo.setTitleEN("Truemove-H");
                            stubInfo.setTitleTH("ทรูมูฟเฮ็ด");
                            stubInfo.setRef1("864895245");
                            stubInfo.setRef1TitleEN("Customer ID");
                            stubInfo.setRef1TitleTH("รหัสลูกค้า");
                            stubInfo.setRef2("9231782945372901");
                            stubInfo.setRef2TitleEN("Billing Number");
                            stubInfo.setRef2TitleTH("เลขที่ใบแจ้งค่าใช้บริการ");
                            stubInfo.setAmount(new BigDecimal(785.65));
                            stubInfo.setServiceFee(sFee);
                            stubInfo.setSourceOfFundFees(sofFees);
                            return stubInfo;
                    }
            };
	}
		
}