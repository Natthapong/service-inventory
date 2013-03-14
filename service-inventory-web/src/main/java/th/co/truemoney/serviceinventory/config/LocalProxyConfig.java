package th.co.truemoney.serviceinventory.config;

import java.math.BigDecimal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.AuthenticateRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.AuthenticateResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.CreateSessionResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.StandardBizResponse;


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
				return null;
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
				return new SignonResponse("1", "0", "namespace", new String[] {"key"}, new String[] {"value"}, "sessionId", "trueMoneyId");
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
}