package th.co.truemoney.serviceinventory.legacyfacade;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileAdminProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ProfileRegisteringHandler;

import com.tmn.core.api.message.CreateTmnProfileRequest;
import com.tmn.core.api.message.CreateTmnProfileResponse;
import com.tmn.core.api.message.IsCreatableRequest;
import com.tmn.core.api.message.StandardBizResponse;

public class TestProfileRegisteringBuilder {
	
	private LegacyFacade legacyFacade;
	private ProfileRegisteringHandler profileRegisteringFacade;
	private TmnProfileAdminProxyClient tmnProfileAdminProxyMock;
	private TmnProfileProxyClient tmnProfileProxyMock;
	
	@Before
	public void before() {
		this.tmnProfileAdminProxyMock = mock(TmnProfileAdminProxyClient.class);
		this.tmnProfileProxyMock = mock(TmnProfileProxyClient.class);
		
		this.legacyFacade = new LegacyFacade();
		this.profileRegisteringFacade = new ProfileRegisteringHandler();
		this.profileRegisteringFacade.setTmnProfileAdminProxy(tmnProfileAdminProxyMock);
		this.profileRegisteringFacade.setTmnProfileProxy(tmnProfileProxyMock);
		this.legacyFacade.setProfileRegisteringFacade(profileRegisteringFacade);
	}
	
	@After
	public void after() {
		reset(tmnProfileAdminProxyMock);
		reset(tmnProfileProxyMock);
	}
	
	@Test
	public void verifyEmailSuccess() {
		//given 
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class)))
			.thenReturn(createStubbedStandardBizResponse());
		
		//when
		legacyFacade.registering()
			.fromChannel(41)
			.verifyEmail("tmn@gmail.com");
		
		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
	}

	@Test(expected=ServiceInventoryException.class)
	public void verifyEmailFail() {
		//given 
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class)))
			.thenThrow(new ServiceInventoryException());
		
		//when
		legacyFacade.registering()
			.fromChannel(41)
			.verifyEmail("tmn@gmail.com");
		
		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
	}
	
	@Test
	public void verifyMobileSuccess() {
		//given 
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class)))
			.thenReturn(createStubbedStandardBizResponse());
		
		//when
		legacyFacade.registering()
			.fromChannel(41)
			.verifyMobileNumber("08xxxxxxxx");
		
		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
	}
	
	@Test(expected=ServiceInventoryException.class)
	public void verifyMobileFail() {
		//given 
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class)))
			.thenThrow(new ServiceInventoryException());
		
		//when
		legacyFacade.registering()
			.fromChannel(41)
			.verifyMobileNumber("08xxxxxxxx");
		
		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
	}

	@Test
	public void registerSuccess() {
		//given 
		when(tmnProfileProxyMock.createTmnProfile(any(CreateTmnProfileRequest.class)))
			.thenReturn(createStubbedCreateTmnProfileResponse());
		
		//when
		TmnProfile tmnProfile = new TmnProfile("tmn@gmail.com", "password5555", "Fullname",
				"thaiID", "08xxxxxxxx", BigDecimal.ZERO);
		legacyFacade.fromChannel(41)
			.registering()
			.register(tmnProfile);
		
		//then
		verify(tmnProfileProxyMock).createTmnProfile(any(CreateTmnProfileRequest.class));
	}
	
	@Test(expected=FailResultCodeException.class)
	public void registerFailWithResultCode() {
		//given 
		when(tmnProfileProxyMock.createTmnProfile(any(CreateTmnProfileRequest.class)))
			.thenThrow(new FailResultCodeException("1", "Core"));
		
		//when
		TmnProfile tmnProfile = new TmnProfile("tmn@gmail.com", "password5555", "Fullname",
				"thaiID", "08xxxxxxxx", BigDecimal.ZERO);
		legacyFacade.fromChannel(41)
			.registering()
			.register(tmnProfile);
		
		//then
		verify(tmnProfileProxyMock).createTmnProfile(any(CreateTmnProfileRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void registerFailWithUnExpectedCode() {
		//given 
		when(tmnProfileProxyMock.createTmnProfile(any(CreateTmnProfileRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		TmnProfile tmnProfile = new TmnProfile("tmn@gmail.com", "password5555", "Fullname",
				"thaiID", "08xxxxxxxx", BigDecimal.ZERO);
		legacyFacade.fromChannel(41)
			.registering()
			.register(tmnProfile);
		
		//then
		verify(tmnProfileProxyMock).createTmnProfile(any(CreateTmnProfileRequest.class));
	}
	
	private CreateTmnProfileResponse createStubbedCreateTmnProfileResponse() {
		CreateTmnProfileResponse createTmnProfileResponse = new CreateTmnProfileResponse();
		createTmnProfileResponse.setTransactionId("transactionID");
		createTmnProfileResponse.setResultCode("0");
		createTmnProfileResponse.setResultNamespace("core");
		return createTmnProfileResponse;
	}

	private StandardBizResponse createStubbedStandardBizResponse() {
		StandardBizResponse standardBizResponse = new StandardBizResponse();
		standardBizResponse.setTransactionId("transactionID");
		standardBizResponse.setResultCode("0");
		standardBizResponse.setResultNamespace("core");
		return standardBizResponse;
	}

}
