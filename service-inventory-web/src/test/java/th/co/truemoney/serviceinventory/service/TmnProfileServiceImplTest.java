package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.impl.TmnProfileProxyImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.impl.TmnSecurityProxyImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.stub.TmnProfileStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TmnProfileServiceImplTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;	
	private TmnSecurityProxy tmnSecurityProxyMock;	
	private TmnProfileProxy tmnProfileProxyMock;	
	
	@Before
	public void setup() {
		this.tmnProfileServiceImpl = new TmnProfileServiceImpl();
		this.tmnSecurityProxyMock = Mockito.mock(TmnSecurityProxyImpl.class);
		this.tmnProfileProxyMock = Mockito.mock(TmnProfileProxyImpl.class);
		
		this.tmnProfileServiceImpl.setTmnSecurityProxy(tmnSecurityProxyMock);
		this.tmnProfileServiceImpl.setTmnProfileProxy(tmnProfileProxyMock);
		this.tmnProfileServiceImpl.setAccessTokenRepository(new AccessTokenMemoryRepository());
	}
	
	@Test
	public void shouldLoginSuccess() {
		
		//given
		SignonResponse stubbedSignonResponse = TmnProfileStubbed.createSuccessStubbedSignonResponse();
		
		GetBasicProfileResponse stubbedProfileResponse = TmnProfileStubbed.createSuccessStubbedProfileResponse();

		when(tmnSecurityProxyMock.signon(Mockito.any(SignonRequest.class))).thenReturn(stubbedSignonResponse);	
		
		when(tmnProfileProxyMock.getBasicProfile(Mockito.any(th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.StandardBizRequest.class))).thenReturn(stubbedProfileResponse);	
		
		//when
		Login login = new Login("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
		String result = this.tmnProfileServiceImpl.login(41, login);

		//then
		assertNotNull(result);

	}
	
	//@Test
	public void shouldLoginFailWithSignon() {		
		
		//given
		when(tmnSecurityProxyMock.signon(any(SignonRequest.class)))
			.thenThrow(TmnProfileStubbed.createFailedThrowEwalletException());
		
		//when
		try {

			Login login = new Login("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
			this.tmnProfileServiceImpl.login(41, login);
			Assert.fail();
		} catch (SignonServiceException ex) {
			assertEquals("error code", ex.getCode());
			assertEquals("error namespace", ex.getNamespace());
		}
		
		//then
		verify(tmnSecurityProxyMock).signon(any(SignonRequest.class));

	}
		
}
