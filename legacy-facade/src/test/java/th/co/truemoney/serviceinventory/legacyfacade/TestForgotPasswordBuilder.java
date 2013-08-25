package th.co.truemoney.serviceinventory.legacyfacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ForgotPasswordHandler;

public class TestForgotPasswordBuilder {

	private LegacyFacade legacyFacade;
	private ForgotPasswordHandler forgotPasswordFacade;
	private AccessToken accessToken;
	private TmnProfileAdminProxy tmnProfileAdminProxyMock;
	
	@Before
	public void before() {
        this.accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
		this.tmnProfileAdminProxyMock = mock(TmnProfileAdminProxy.class);
		
		this.legacyFacade = new LegacyFacade();
		this.forgotPasswordFacade = new ForgotPasswordHandler();
		this.forgotPasswordFacade.setTmnProfileAdminProxy(tmnProfileAdminProxyMock);
		this.legacyFacade.setForgotPasswordFacade(forgotPasswordFacade);
	}
	
	@After
	public void after() {
		reset(tmnProfileAdminProxyMock);
	}
	
	@Test
	public void createForgotPasswordSuccess() {
		//given 
		when(tmnProfileAdminProxyMock.createForgotPassword(any(CreateForgotPasswordRequest.class)))
			.thenReturn(createStubbedCreateForgotPasswordResponse());
		
		//when
		legacyFacade.forgotPassword()
			.fromChannel(accessToken.getChannelID())
			.withLogin(accessToken.getLoginID())
			.withIdCardNumber("1212121212121")
			.createForgotPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).createForgotPassword(any(CreateForgotPasswordRequest.class));
	}

	@Test(expected=FailResultCodeException.class)
	public void createForgotPasswordFailWithResultCode() {
		//given 
		when(tmnProfileAdminProxyMock.createForgotPassword(any(CreateForgotPasswordRequest.class)))
			.thenThrow(new FailResultCodeException("1", "Core"));
		
		//when
		legacyFacade.forgotPassword()
			.fromChannel(accessToken.getChannelID())
			.withLogin(accessToken.getLoginID())
			.withIdCardNumber("1212121212121")
			.createForgotPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).createForgotPassword(any(CreateForgotPasswordRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void createForgotPasswordFailWithUnExpectedCode() {
		//given 
		when(tmnProfileAdminProxyMock.createForgotPassword(any(CreateForgotPasswordRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.forgotPassword()
			.fromChannel(accessToken.getChannelID())
			.withLogin(accessToken.getLoginID())
			.withIdCardNumber("1212121212121")
			.createForgotPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).createForgotPassword(any(CreateForgotPasswordRequest.class));
	}
	
	@Test
	public void verifyResetPasswordSuccess() {
		//given 
		when(tmnProfileAdminProxyMock.verifyForgotPassword(any(VerifyForgotPasswordRequest.class)))
			.thenReturn(createStubbedVerifyForgotPasswordResponse());
		
		//when
		ResetPassword resetPassword = legacyFacade.forgotPassword()
				   .fromChannel(accessToken.getChannelID())
				   .withToken("forgotToken")
				   .verifyResetPassword();
		
		//then
		assertNotNull(resetPassword);
		assertEquals("loginID", resetPassword.getLoginID());
		assertEquals("truemoneyID", resetPassword.getTruemoneyID());
		assertEquals("08xxxxxxxx", resetPassword.getMobileNumber());
		verify(tmnProfileAdminProxyMock).verifyForgotPassword(any(VerifyForgotPasswordRequest.class));
	}
	
	@Test(expected=FailResultCodeException.class)
	public void verifyResetPasswordFailWithResultCode() {
		//given 
		when(tmnProfileAdminProxyMock.verifyForgotPassword(any(VerifyForgotPasswordRequest.class)))
			.thenThrow(new FailResultCodeException("1", "Core"));
		
		//when
		legacyFacade.forgotPassword()
		   .fromChannel(accessToken.getChannelID())
		   .withToken("forgotToken")
		   .verifyResetPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).verifyForgotPassword(any(VerifyForgotPasswordRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void verifyResetPasswordFailWithUnExpectedCode() {
		//given 
		when(tmnProfileAdminProxyMock.verifyForgotPassword(any(VerifyForgotPasswordRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		legacyFacade.forgotPassword()
		   .fromChannel(accessToken.getChannelID())
		   .withToken("forgotToken")
		   .verifyResetPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).verifyForgotPassword(any(VerifyForgotPasswordRequest.class));
	}
	
	@Test
	public void confirmResetPasswordSuccess() {
		//given 
		when(tmnProfileAdminProxyMock.confirmForgotPassword(any(ConfirmForgotPasswordRequest.class)))
			.thenReturn(createStubbedStandardBizResponse());
		
		//when
		ResetPassword resetPassword = new ResetPassword("forgotToken", "newPassword");
		resetPassword.setLoginID(accessToken.getLoginID());
		legacyFacade.forgotPassword()
		   .fromChannel(accessToken.getChannelID())
		   .withToken(resetPassword.getToken())
		   .withNewPassword(resetPassword.getLoginID(), resetPassword.getNewPassword())
		   .confirmResetPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).confirmForgotPassword(any(ConfirmForgotPasswordRequest.class));
	}
	
	@Test(expected=FailResultCodeException.class)
	public void confirmResetPasswordFailWithResultCode() {
		//given 
		when(tmnProfileAdminProxyMock.confirmForgotPassword(any(ConfirmForgotPasswordRequest.class)))
			.thenThrow(new FailResultCodeException("1", "Core"));
		
		//when
		ResetPassword resetPassword = new ResetPassword("forgotToken", "newPassword");
		resetPassword.setLoginID(accessToken.getLoginID());
		legacyFacade.forgotPassword()
		   .fromChannel(accessToken.getChannelID())
		   .withToken(resetPassword.getToken())
		   .withNewPassword(resetPassword.getLoginID(), resetPassword.getNewPassword())
		   .confirmResetPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).confirmForgotPassword(any(ConfirmForgotPasswordRequest.class));
	}
	
	@Test(expected=EwalletUnExpectedException.class)
	public void confirmResetPasswordFailWithUnExpectedCode() {
		//given 
		when(tmnProfileAdminProxyMock.confirmForgotPassword(any(ConfirmForgotPasswordRequest.class)))
			.thenThrow(new EwalletUnExpectedException(new Exception()));
		
		//when
		ResetPassword resetPassword = new ResetPassword("forgotToken", "newPassword");
		resetPassword.setLoginID(accessToken.getLoginID());
		legacyFacade.forgotPassword()
		   .fromChannel(accessToken.getChannelID())
		   .withToken(resetPassword.getToken())
		   .withNewPassword(resetPassword.getLoginID(), resetPassword.getNewPassword())
		   .confirmResetPassword();
		
		//then
		verify(tmnProfileAdminProxyMock).confirmForgotPassword(any(ConfirmForgotPasswordRequest.class));
	}
	
	private StandardBizResponse createStubbedStandardBizResponse() {
		StandardBizResponse standardBizResponse = new StandardBizResponse();
		standardBizResponse.setTransactionId("transactionID");
		standardBizResponse.setResultCode("0");
		standardBizResponse.setResultNamespace("core");
		return standardBizResponse;
	}

	private VerifyForgotPasswordResponse createStubbedVerifyForgotPasswordResponse() {
		VerifyForgotPasswordResponse forgotPasswordResponse = new VerifyForgotPasswordResponse();
		forgotPasswordResponse.setTransactionId("transactionID");
		forgotPasswordResponse.setResultCode("0");
		forgotPasswordResponse.setResultNamespace("core");
		forgotPasswordResponse.setTmnId("truemoneyID");
		forgotPasswordResponse.setLoginId("loginID");
		forgotPasswordResponse.setMobile("08xxxxxxxx");		
		return forgotPasswordResponse;
	}

	private CreateForgotPasswordResponse createStubbedCreateForgotPasswordResponse() {
		CreateForgotPasswordResponse forgotPasswordResponse = new CreateForgotPasswordResponse();
		forgotPasswordResponse.setTransactionId("transactionID");
		forgotPasswordResponse.setResultCode("0");
		forgotPasswordResponse.setResultNamespace("core");
		forgotPasswordResponse.setForgotToken("forgotToken");
		return forgotPasswordResponse;
	}
	
}
