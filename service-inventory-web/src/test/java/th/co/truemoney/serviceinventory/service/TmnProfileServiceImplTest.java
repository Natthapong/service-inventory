package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
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
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.impl.TmnSecurityProxyImpl;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.stub.TmnProfileStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TmnProfileServiceImplTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;	
	private TmnSecurityProxy tmnSecurityProxyMock;	
	
	@Before
	public void setup() {
		this.tmnProfileServiceImpl = new TmnProfileServiceImpl();
		this.tmnSecurityProxyMock = Mockito.mock(TmnSecurityProxyImpl.class);

		this.tmnProfileServiceImpl.setTmnSecurityProxy(tmnSecurityProxyMock);
	}
	
	@Test
	public void shouldLoginSuccess() {
		
		//given
		SignonResponse stubbedSignonResponse = TmnProfileStubbed.createSuccessStubbedSignonResponse();

		when(tmnSecurityProxyMock.signon(Mockito.any(SignonRequest.class))).thenReturn(stubbedSignonResponse);	
		
		//when
		Login login = new Login("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
		String result = this.tmnProfileServiceImpl.login(login, 41, "1AB", "iphone", "6.1", "192.168.1.1");

		//then
		assertEquals(result, "8e48e03be057319f40621fe9bcd123f750f6df1d");

	}
	
	@Test
	public void shouldLoginFailWithSignon() {		
		
		//given
		when(tmnSecurityProxyMock.signon(any(SignonRequest.class)))
			.thenThrow(TmnProfileStubbed.createFailedThrowEwalletException());
		
		//when
		try {

			Login login = new Login("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
			this.tmnProfileServiceImpl.login(login, 41, "1AB", "iphone", "6.1", "192.168.1.1");
			Assert.fail();
		} catch (SignonServiceException ex) {
			assertEquals("error code", ex.getCode());
			assertEquals("error namespace", ex.getNamespace());
		}
		
		//then
		verify(tmnSecurityProxyMock).signon(any(SignonRequest.class));

	}
		
}
