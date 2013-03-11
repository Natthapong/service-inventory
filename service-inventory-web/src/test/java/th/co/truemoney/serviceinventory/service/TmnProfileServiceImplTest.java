package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.profile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.profile.impl.TmnProfileProxyImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.impl.TmnSecurityProxyImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.stub.TmnProfileStubbed;

@RunWith(MockitoJUnitRunner.class)
public class TmnProfileServiceImplTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;	
	private TmnProfileProxy tmnProfileProxyMock;
	private TmnSecurityProxy tmnSecurityProxyMock;	
	
	@Before
	public void setup() {
		this.tmnProfileServiceImpl = new TmnProfileServiceImpl();
		this.tmnSecurityProxyMock = Mockito.mock(TmnSecurityProxyImpl.class);
		this.tmnProfileProxyMock = Mockito.mock(TmnProfileProxyImpl.class);
		
		this.tmnProfileServiceImpl.setTmnProfileProxy(tmnProfileProxyMock);
		this.tmnProfileServiceImpl.setTmnSecurityProxy(tmnSecurityProxyMock);
	}
	
	@Test
	public void shouldLoginSuccess() {
		
		//given
		SignonResponse stubbedSignonResponse = TmnProfileStubbed.createSuccessStubbedSignonResponse();
		GetBasicProfileResponse stubbedProfileResponse = TmnProfileStubbed.createSuccessStubbedProfileResponse();
		
		when(tmnSecurityProxyMock.signon(Mockito.any(SignonRequest.class)))
			.thenReturn(stubbedSignonResponse);	
		when(tmnProfileProxyMock.getBasicProfile(Mockito.any(StandardBizRequest.class)))
			.thenReturn(stubbedProfileResponse);	
		
		//when
		Login login = new Login("mali@hotmail.com", "0000");
		ServiceResponse<TmnProfile> result = this.tmnProfileServiceImpl.login(login);

		//then
		assertEquals(ServiceInventoryException.Code.SUCCESS, result.getResponseCode());
		assertEquals(ServiceInventoryException.NAMESPACE, result.getResponseNamespace());
		assertEquals("SjdfgkIDF", result.getBody().getSessionID());
		assertEquals("tmnid0001", result.getBody().getTruemoneyID());
//		assertEquals("Mali Colt", result.getBody().getFullname());
//		assertEquals("30000.00", result.getBody().getEwalletBalance());		
	}
	
	@Test
	public void shouldLoginFailWithSignon() {		
		
		//given
		when(tmnSecurityProxyMock.signon(any(SignonRequest.class)))
			.thenThrow(TmnProfileStubbed.createFailedThrowServiceInventoryException());
		
		//when
		try {

			Login login = new Login("mali@hotmail.com", "0000");
			this.tmnProfileServiceImpl.login(login);
			Assert.fail();
		} catch (ServiceInventoryException ex) {
			assertEquals("error code", ex.getCode());
			assertEquals("error description", ex.getDescription());
			assertEquals("error namespace", ex.getNamespace());
		}
		
		//then
		verify(tmnSecurityProxyMock).signon(any(SignonRequest.class));
		verify(tmnProfileProxyMock, never()).getBasicProfile(any(StandardBizRequest.class));
		
	}
	


//	@Test
//	public void shouldLoginFailWithGetBasicProfile() {		
//		
//		//given 
//		when(tmnSecurityProxyMock.signon(any(SignonRequest.class)))
//			.thenReturn(TmnProfileStubbed.createSuccessStubbedSignonResponse());
//		when(tmnProfileProxyMock.getBasicProfile(any(StandardBizRequest.class)))
//			.thenThrow(TmnProfileStubbed.createFailedThrowServiceInventoryException());
//		
//		//when 
//		try {
//			Login login = new Login("mali@hotmail.com","0000");
//			this.tmnProfileServiceImpl.login(login);
//			Assert.fail();
//		} catch (ServiceInventoryException ex) {
//			assertEquals("error code", ex.getCode());
//			assertEquals("error description", ex.getDescription());
//			assertEquals("error namespace", ex.getNamespace());
//		}
//		
//		//then
//		verify(tmnSecurityProxyMock).signon(any(SignonRequest.class));
//		verify(tmnProfileProxyMock).getBasicProfile(any(StandardBizRequest.class));
//
//	}
	
//	@Test
//	public void shouldLoginFailWithNotCustomerProfile() {		
//		
//		//given 
//		when(tmnSecurityProxyMock.signon(any(SignonRequest.class)))
//			.thenReturn(TmnProfileStubbed.createSuccessStubbedSignonResponse());
//		when(tmnProfileProxyMock.getBasicProfile(any(StandardBizRequest.class)))
//			.thenReturn(TmnProfileStubbed.createFailedNotCustomerProfile());
//		
//		//when 
//		try {
//			Login login = new Login("mali@hotmail.com","0000");
//			this.tmnProfileServiceImpl.login(login);
//			Assert.fail();
//		} catch (ServiceInventoryException ex) {
//			assertEquals("10000", ex.getCode());
//			assertEquals("TMN-SERVICE-INVENTORY", ex.getNamespace());
//		}
//		
//		//then
//		verify(tmnSecurityProxyMock).signon(any(SignonRequest.class));
//		verify(tmnProfileProxyMock).getBasicProfile(any(StandardBizRequest.class));
//
//	}
	
	@Test
	public void shouldExtendSuccess() {		

		//given
		when(tmnSecurityProxyMock.extendSession(any(StandardBizRequest.class)))
			.thenReturn(TmnProfileStubbed.createSuccessStubbedStandardBizResponse());
				
		//when
		TmnProfile tmnProfile = new TmnProfile("SjdfgkIDF", "tmnid0001");
		tmnProfile.setFullname("Mali Colt");
		tmnProfile.setEwalletBalance("30000.00");
		ServiceResponse<StandardBizResponse> result = this.tmnProfileServiceImpl.extend(tmnProfile);

		//then
		assertEquals(ServiceInventoryException.Code.SUCCESS, result.getResponseCode());
		assertEquals(ServiceInventoryException.NAMESPACE, result.getResponseNamespace());
		
	}
	
	@Test
	public void shouldExtendFail() {		
		
		//given
		when(tmnSecurityProxyMock.extendSession(any(StandardBizRequest.class)))
			.thenThrow(TmnProfileStubbed.createFailedThrowServiceInventoryException());
						
		//when
		try {
			TmnProfile tmnProfile = new TmnProfile("SjdfgkIDF", "tmnid0001");
			tmnProfile.setFullname("Mali Colt");
			tmnProfile.setEwalletBalance("30000.00");
			this.tmnProfileServiceImpl.extend(tmnProfile);
			Assert.fail();
		} catch (ServiceInventoryException ex) {
			assertEquals("error code", ex.getCode());
			assertEquals("error description", ex.getDescription());
			assertEquals("error namespace", ex.getNamespace());
		}
		
		//then
		verify(tmnSecurityProxyMock).extendSession(any(StandardBizRequest.class));

	}

	@Test
	public void shouldLogoutSuccess() {		
		
		//given
		when(tmnSecurityProxyMock.extendSession(any(StandardBizRequest.class)))
			.thenReturn(TmnProfileStubbed.createSuccessStubbedStandardBizResponse());
		
		//when
		TmnProfile tmnProfile = new TmnProfile("SjdfgkIDF", "tmnid0001");
		tmnProfile.setFullname("Mali Colt");
		tmnProfile.setEwalletBalance("30000.00");
		ServiceResponse<StandardBizResponse> result = this.tmnProfileServiceImpl.logout(tmnProfile);

		//then
		assertEquals(ServiceInventoryException.Code.SUCCESS, result.getResponseCode());
		assertEquals(ServiceInventoryException.NAMESPACE, result.getResponseNamespace());
	}
	
	@Test
	public void shouldLogoutFail() {
		
		//given
		when(tmnSecurityProxyMock.terminateSession(any(StandardBizRequest.class)))
			.thenThrow(TmnProfileStubbed.createFailedThrowServiceInventoryException());
				
		//when
		try {
			TmnProfile tmnProfile = new TmnProfile("SjdfgkIDF", "tmnid0001");
			tmnProfile.setFullname("Mali Colt");
			tmnProfile.setEwalletBalance("30000.00");
			this.tmnProfileServiceImpl.logout(tmnProfile);
			Assert.fail();
		} catch (ServiceInventoryException ex) {
			assertEquals("error code", ex.getCode());
			assertEquals("error description", ex.getDescription());
			assertEquals("error namespace", ex.getNamespace());
		}
		
		//then 
		verify(tmnSecurityProxyMock).terminateSession(any(StandardBizRequest.class));
		
	}
	

	
}
