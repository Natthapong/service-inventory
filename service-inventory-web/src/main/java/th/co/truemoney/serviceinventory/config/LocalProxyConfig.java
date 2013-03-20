package th.co.truemoney.serviceinventory.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.firsthop.proxy.impl.SmsProxyImpl;


@Configuration
@Profile("local")
public class LocalProxyConfig {

	@Bean @Primary
	public TmnProfileProxy stubTmnProfileProxy() {
		return new TmnProfileProxy() {

			@Override
			public GetBasicProfileResponse getBasicProfile(
					StandardBizRequest standardBizRequest) throws EwalletException {
				return new GetBasicProfileResponse("1", "0", "namespace",
						new String[] {"key"}, new String[] {"value"},
						"Firstname lastname", "local@tmn.com", "0891231234",
						new BigDecimal(50.0d),
						"C",
						3);
			}

			@Override
			public CreateTmnProfileResponse createTmnProfile(
					CreateTmnProfileRequest createTmnProfileRequest)
					throws EwalletException {
				return new CreateTmnProfileResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"}, "123123");
			}

			@Override
			public ListSourceResponse listSource(
					ListSourceRequest listSourceRequest)
					throws EwalletException {
				
				SourceContext[] sourceContext = new SourceContext[3];
				sourceContext[0] = new SourceContext("1","type",new String[] {"SCB","xxxx1234"});
				sourceContext[1] = new SourceContext("2","type",new String[] {"KTB","xxxx5678"});
				sourceContext[2] = new SourceContext("3","type",new String[] {"BBL","xxxx9101"});

				return new ListSourceResponse("1", "2", "namespace", new String[] {"key"}, new String[] {"value"}, sourceContext);
			}
		};
	}

	@Bean @Primary
	public TmnSecurityProxy stubTmnSecurityProxy() {
		return new TmnSecurityProxy() {

			@Override
			public StandardBizResponse terminateSession(
					th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				return new StandardBizResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"});
			}

			@Override
			public SignonResponse signon(SignonRequest signOnRequest)
					throws EwalletException {


				String username = signOnRequest.getInitiator();
				String password = signOnRequest.getPin();

				if ("local@tmn.com".equals(username) &&  "password".equals(password)) {
					return new SignonResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"}, "sessionId", "trueMoneyId");
				}

				throw new SignonServiceException("4", "");
			}

			@Override
			public StandardBizResponse extendSession(
					th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				return new StandardBizResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"});
			}

			@Override
			public CreateSessionResponse createSession() throws EwalletException {
				return new CreateSessionResponse("0", "namespace", "sessionId");
			}

			@Override
			public AuthenticateResponse authenticate(
					AuthenticateRequest authenticateRequest) throws EwalletException {
				return new AuthenticateResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"}, "trueMoneyId");
			}
		};
	}

	@Bean @Primary
	public OrderRepository stubOrderRepository(){
		return new OrderMemoryRepository(){
			public TopUpQuote getTopUpQuote(String orderID) {
				return new TopUpQuote();
			}
		};
	}
	
	@Bean @Primary
	public SourceOfFundRepository stubSourceOfFundRepository(){
		return new SourceOfFundRepository(){
			public DirectDebit getUserDirectDebitSourceByID(String sourceOfFundID, String truemoneyID, Integer channelID, String sessionID) {
				return new DirectDebit("SCB","Siam Commercial Bank","ไทยพาณิชย์","xxxx1234",new BigDecimal(30),new BigDecimal(5000));
			}
		};
	}
	
	@Bean @Primary
	public DirectDebitConfig stubDirectDebitConfig(){
		return new DirectDebitConfigImpl(){
			private HashMap<String, DirectDebitConfigBean> bankConfigList;
			
			public DirectDebitConfigBean getBankDetail(String bankCode) {
				
				try {
					JsonFactory factory = new JsonFactory();
					ObjectMapper m = new ObjectMapper(factory);

					TypeReference<HashMap<String, DirectDebitConfigBean>> typeRef;
					typeRef = new TypeReference<HashMap<String, DirectDebitConfigBean>>() {
					};
					ClassPathResource resource = new ClassPathResource("addmoney/directdebit.json");
					bankConfigList = m.readValue(resource.getFile(), typeRef);

				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return bankConfigList.get(bankCode);
			}
		};
	}
	
	@Bean @Primary
	public EwalletSoapProxy stubEWalletSoapProxy() {
		return new EwalletSoapProxy() {

			@Override
			public StandardMoneyResponse verifyAddMoney(
					VerifyAddMoneyRequest verifyAddMoneyRequest)
					throws EwalletException {
				return new StandardMoneyResponse("1234", "0", "namespce", new String[] {"key"}, new String[] {"value"}, "stub@local.com", new BigDecimal(100.00));
			}

			@Override
			public GetBalanceResponse getBalance(
					th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				return new GetBalanceResponse("1234", "0", "namespce", new String[] {"key"}, new String[] {"value"}, new BigDecimal("2000.00"), new BigDecimal("2000.00"), new BigDecimal("2000.00"));
			}

			@Override
			public StandardMoneyResponse addMoney(
					AddMoneyRequest addMoneyRequest) throws EwalletException {
				// TODO Auto-generated method stub
				//Thread.sleep(1000);
				return null;
			}

		};
	}

	@Bean @Primary
	public SmsProxy stubSmsProxy() {
		return new SmsProxy() {
			
			@Override
			public SmsResponse send(SmsRequest request) {
				StringBuilder xmlResponse = new StringBuilder();
				xmlResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				xmlResponse.append("<message id=\"13619484376108002\">");
				xmlResponse.append("<rsr type=\"ack\">");
				xmlResponse.append("<service-id/>");
				xmlResponse.append("<destination messageid=\"13619484376108002\">");
				xmlResponse.append("<address>");
				xmlResponse.append("<number type=\"international\">66891267026</number>");
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
				
				return new SmsProxyImpl().readXMLResponse(xmlResponse.toString());
			}
						
		};
	}
}