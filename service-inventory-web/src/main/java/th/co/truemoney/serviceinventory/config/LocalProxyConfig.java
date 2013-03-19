package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;


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
						"Firstname lastname", "0891231234",
						new BigDecimal(50.0d),
						"customer",
						1);
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

				SourceContext[] sourceContext = new SourceContext[1];
				sourceContext[0] = new SourceContext("3","type",new String[] {"SCB","TMB","BBL"});
				return new ListSourceResponse("1", "2", "namespace", new String[] {"key"}, new String[] {"value"}, sourceContext);
			}
		};
	}

	@Bean @Primary
	public TmnSecurityProxy stubTmnSecurityProxy() {
		return new TmnSecurityProxy() {

			@Override
			public StandardBizResponse terminateSession(
					th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.StandardBizRequest standardBizRequest)
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

				throw new SignonServiceException("500", "un authorize");
			}

			@Override
			public StandardBizResponse extendSession(
					th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.StandardBizRequest standardBizRequest)
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
					th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.message.StandardBizRequest standardBizRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StandardMoneyResponse addMoney(AddMoneyRequest addMoneyRequest)
					throws EwalletException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}