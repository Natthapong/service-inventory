package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.email.StubEmailService;
import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
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
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.impl.SmsProxyImpl;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {

	@Bean
	@Primary
	public P2PTransferService stubP2PTransferService(){
		return new P2PTransferServiceImpl(){

			@Override
			public Transaction.Status getTransactionStatus(String transactionID, String accessTokenID) {
				if(transactionID.equals("0000")){
					return Transaction.Status.VERIFIED;
				}else{
					return Transaction.Status.FAILED;
				}
			}

			@Override
			public OTP sendOTP(String draftTransactionID,
					String accessTokenID) {
				if(accessTokenID.equals("12345")){
					return new OTP("0868185055", "111111", "marty");
				}else{
					throw new ServiceInventoryException("9999","Can't send OTP","SI-WEB");
				}
			}

			@Override
			public P2PDraftTransaction getDraftTransactionDetails(
					String draftTransactionID, String accessTokenID) {
				if(accessTokenID.equals("12345")){
					return new P2PDraftTransaction("0868185055", new BigDecimal(2500),"555","12345","Mart FullName","111111");
				}else{
					throw new ServiceInventoryException("9999","No Draft Transaction","SI-WEB");
				}
			}

			@Override
			public P2PDraftTransaction createDraftTransaction(String mobileNumber, BigDecimal amount, String accessTokenID) {

				if(accessTokenID.equals("12345")){
					return new P2PDraftTransaction("0868185055",new BigDecimal(2500),"555","12345","fullName","111111");
				}else{
					throw new ServiceInventoryException("9999","Can not create Draft Transaction","SI-WEB");
				}
			}
		};
	}

	@Bean
	@Primary
	public TmnProfileProxy stubTmnProfileProxy() {
		return new TmnProfileProxy() {

			@Override
			public GetBasicProfileResponse getBasicProfile(
					StandardBizRequest standardBizRequest)
					throws EwalletException {
				return new GetBasicProfileResponse("1", "0", "namespace",
						new String[] { "key" }, new String[] { "value" },
						"username", "local@tmn.com", "0891231234",
						new BigDecimal(50.0d), "C", 3);
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

				if ("local@tmn.com".equals(initiator)
						&& "password".equals(password)) {
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
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StandardMoneyResponse verifyTransfer(
					VerifyTransferRequest verifyTransferRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
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
				if(isCreatableRequest.getLoginId().equals("user1@test.com")){
					throw new ServiceInventoryException("40000","This user already register.");
				}else{
					return new StandardBizResponse("1", "0", "namespace",
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
	public EmailService emailService(){
		return new StubEmailService();
	}
}