package th.co.truemoney.serviceinventory.service;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class TmnRegisterImpServicelTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;
	private TmnProfileProxy tmnProfileProxy;
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	private ProfileRepository profileRepository;
	private OTPService otpService;

	@Before
	public void setup() {
		tmnProfileServiceImpl = new TmnProfileServiceImpl();
		tmnProfileAdminProxy = mock(TmnProfileAdminProxy.class);
		tmnProfileProxy = mock(TmnProfileProxy.class);
		profileRepository = mock(ProfileRepository.class);
		otpService = mock(OTPService.class);
	}

	@Test
	public void validateEmailSuccess() throws RemoteException {
		String email = "user1.test.v1@gmail.com";
		StandardBizResponse bizResponse = new StandardBizResponse();
		bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
		when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenReturn(bizResponse);

		tmnProfileServiceImpl.setTmnProfileAdminProxy(tmnProfileAdminProxy);
		String result = tmnProfileServiceImpl.validateEmail(41, email);

		assertEquals(result , email);
	}

	@Test
	public void validateEmailThrowsServiceUnavailableException() {
		try {
			String email = "user1.test.v1@gmail.com";
			StandardBizResponse bizResponse = new StandardBizResponse();
			bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
			when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenThrow(new ServiceUnavailableException(new RemoteException()));

			tmnProfileServiceImpl.setTmnProfileAdminProxy(tmnProfileAdminProxy);
			tmnProfileServiceImpl.validateEmail(41, email);
		} catch (ServiceInventoryException exception) {
			assertEquals("EWALLET-PROXY", exception.getNamespace());
		}
	}

	@Test
	public void validateEmailThrowsEwalletException() {
		try {
			String email = "user1.test.v1@gmail.com";
			StandardBizResponse bizResponse = new StandardBizResponse();
			bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
			when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenThrow(new EwalletException("12345", "EWALLET-PROXY"));

			tmnProfileServiceImpl.setTmnProfileAdminProxy(tmnProfileAdminProxy);
			tmnProfileServiceImpl.validateEmail(41, email);
		} catch (ServiceInventoryException exception) {
			assertEquals("12345", exception.getCode());
		}
	}

	@Test
	public void confirmCreateProfile() {
		OTP otp = new OTP();
		otp.setOtpString("otpString");
		CreateTmnProfileResponse profileResponse = new CreateTmnProfileResponse();
		profileResponse.setResultCode("0");
		profileResponse.setResultNamespace("resultNamespace");
		profileResponse.setTmnId("tmnId");
		profileResponse.setTransactionId("transactionId");

		when(tmnProfileProxy.createTmnProfile(any(CreateTmnProfileRequest.class))).thenReturn(profileResponse);
		tmnProfileServiceImpl.setTmnProfileProxy(tmnProfileProxy);

		when(profileRepository.getTmnProfile(anyString())).thenReturn(new TmnProfile());
		tmnProfileServiceImpl.setProfileRepository(profileRepository);

		when(otpService.isValidOTP(any(OTP.class))).thenReturn(true);
		tmnProfileServiceImpl.setOtpService(otpService);

		TmnProfile tmnProfile =  tmnProfileServiceImpl.confirmCreateProfile(41, otp);
		assertNotNull(tmnProfile);
	}
}
