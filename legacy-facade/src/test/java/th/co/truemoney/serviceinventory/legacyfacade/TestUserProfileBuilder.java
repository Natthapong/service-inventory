package th.co.truemoney.serviceinventory.legacyfacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler.ProfileNotFoundException;

import com.tmn.core.api.message.GetProfileRequest;
import com.tmn.core.api.message.GetProfileResponse;
import com.tmn.core.api.message.SignonRequest;
import com.tmn.core.api.message.SignonResponse;

public class TestUserProfileBuilder {

	private LegacyFacade legacyFacade;
	private UserProfileHandler userProfileFacade;
    private TmnSecurityProxyClient tmnSecurityProxyClientMock;
    private TmnProfileProxyClient tmnProfileProxyClientMock;
	
	@Before
	public void before() {
		this.tmnSecurityProxyClientMock = mock(TmnSecurityProxyClient.class);
		this.tmnProfileProxyClientMock = mock(TmnProfileProxyClient.class);
		
		this.legacyFacade = new LegacyFacade();
		this.userProfileFacade = new UserProfileHandler();
		this.userProfileFacade.setTmnSecurityProxy(tmnSecurityProxyClientMock);
		this.userProfileFacade.setTmnProfileProxy(tmnProfileProxyClientMock);
		this.legacyFacade.setProfileFacade(userProfileFacade);
	}
	
	@After
	public void after() {
		reset(tmnSecurityProxyClientMock);
		reset(tmnProfileProxyClientMock);
	}
	
	@Test
	public void loginSuccess() {
		//given 
		when(tmnSecurityProxyClientMock.signon(any(SignonRequest.class)))
			.thenReturn(createStubbedSignonResponse());
		when(tmnProfileProxyClientMock.getProfile(any(GetProfileRequest.class)))
			.thenReturn(createStubbedGetProfileResponse());
		
		//when
		AccessToken accessToken = legacyFacade.login(41, "08xxxxxxxx", "password");
		
		//then
		assertNotNull(accessToken);
		assertNotNull(accessToken.getAccessTokenID());
		assertEquals("sessionID", accessToken.getSessionID());
		assertEquals("TruemoneyID", accessToken.getTruemoneyID());
		verify(tmnSecurityProxyClientMock).signon(any(SignonRequest.class));
		verify(tmnProfileProxyClientMock).getProfile(any(GetProfileRequest.class));
	}
	
	@Test(expected=ProfileNotFoundException.class)
	public void loginFailWithProfileNotFound() {
		//given 
		when(tmnSecurityProxyClientMock.signon(any(SignonRequest.class)))
			.thenReturn(createStubbedSignonResponse());
		when(tmnProfileProxyClientMock.getProfile(any(GetProfileRequest.class)))
			.thenReturn(null);
		
		//when
		legacyFacade.login(41, "08xxxxxxxx", "password");
		
		//then
		verify(tmnSecurityProxyClientMock).signon(any(SignonRequest.class));
		verify(tmnProfileProxyClientMock).getProfile(any(GetProfileRequest.class));
	}

	private GetProfileResponse createStubbedGetProfileResponse() {
		GetProfileResponse profileResponse = new GetProfileResponse();
		profileResponse.setTransactionId("transactionID");
		profileResponse.setResultCode("0");
		profileResponse.setResultNamespace("core");
		profileResponse.setEmail("tmn@gmail.com");
		profileResponse.setMobile("08xxxxxxxx");
		profileResponse.setEwalletBalance(BigDecimal.ZERO);
		profileResponse.setHasPassword(Boolean.TRUE);
		profileResponse.setHasPin(Boolean.TRUE);
		return profileResponse;
	}

	private SignonResponse createStubbedSignonResponse() {
		SignonResponse signonResponse = new SignonResponse();
		signonResponse.setTransactionId("transactionID");
		signonResponse.setResultCode("0");
		signonResponse.setResultNamespace("core");
		signonResponse.setTmnId("TruemoneyID");
		signonResponse.setSessionId("sessionID");
		return signonResponse;
	}
	
}
