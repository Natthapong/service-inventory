package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.TestTmnProfileConfig;
import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestTmnProfileConfig.class })
public class TmnRegisterImpServicelTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;
	private TmnProfileProxy tmnProfileProxy;
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	private ProfileRepository profileRepository;
	private OTPService otpService;
	private EmailService emailService;

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;
	private ProfileFacade profileFacade;

	@Before
	public void setup() {
		tmnProfileServiceImpl = new TmnProfileServiceImpl();
		tmnProfileAdminProxy = mock(TmnProfileAdminProxy.class);
		tmnProfileProxy = mock(TmnProfileProxy.class);
		profileRepository = mock(ProfileRepository.class);
		otpService = mock(OTPService.class);
		emailService = mock(EmailService.class);

		profileFacade = new ProfileFacade();
		profileFacade.setTmnProfileAdminProxy(tmnProfileAdminProxy);
		profileFacade.setTmnProfileInitiator(tmnProfileInitiator);
		profileFacade.setTmnProfilePin(tmnProfilePin);

		this.tmnProfileServiceImpl.setProfileFacade(profileFacade);
	}

	@Test
	public void validateEmailSuccess() throws RemoteException {
		String email = "user1.test.v1@gmail.com";
		StandardBizResponse bizResponse = new StandardBizResponse();
		bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
		when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenReturn(bizResponse);

		profileFacade.setTmnProfileAdminProxy(tmnProfileAdminProxy);
		String result = tmnProfileServiceImpl.validateEmail(41, email);

		assertEquals(result , email);
	}

	@Test
	public void validateEmailThrowsServiceUnavailableException() {
		try {
			String email = "user1.test.v1@gmail.com";
			StandardBizResponse bizResponse = new StandardBizResponse();
			bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
			when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenThrow(new EwalletUnExpectedException(new RemoteException()));

			profileFacade.setTmnProfileAdminProxy(tmnProfileAdminProxy);
			tmnProfileServiceImpl.validateEmail(41, email);
		} catch (EwalletUnExpectedException exception) {
			assertEquals("EWALLET-PROXY", exception.getNamespace());
		}
	}

	@Test
	public void validateEmailThrowsEwalletException() {
		try {
			String email = "user1.test.v1@gmail.com";
			StandardBizResponse bizResponse = new StandardBizResponse();
			bizResponse.setResultCode(StandardBizResponse.SUCCESS_CODE);
			when(tmnProfileAdminProxy.isCreatable(any(IsCreatableRequest.class))).thenThrow(new FailResultCodeException("12345", "EWALLET-PROXY"));

			profileFacade.setTmnProfileAdminProxy(tmnProfileAdminProxy);
			tmnProfileServiceImpl.validateEmail(41, email);
		} catch (FailResultCodeException exception) {
			assertEquals("12345", exception.getCode());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void confirmCreateProfile() throws Exception {
		OTP otp = new OTP();
		otp.setOtpString("otpString");
		CreateTmnProfileResponse profileResponse = new CreateTmnProfileResponse();
		profileResponse.setResultCode("0");
		profileResponse.setResultNamespace("resultNamespace");
		profileResponse.setTmnId("tmnId");
		profileResponse.setTransactionId("transactionId");

		when(tmnProfileProxy.createTmnProfile(any(CreateTmnProfileRequest.class))).thenReturn(profileResponse);
		profileFacade.setTmnProfileProxy(tmnProfileProxy);

		TmnProfile stubbedTmnProfile = new TmnProfile();
		stubbedTmnProfile.setEmail("email@gmail.com");
		when(profileRepository.getTmnProfile(anyString())).thenReturn(stubbedTmnProfile);
		tmnProfileServiceImpl.setProfileRepository(profileRepository);

		when(otpService.isValidOTP(any(OTP.class))).thenReturn(true);
		tmnProfileServiceImpl.setOtpService(otpService);

		doNothing().when(emailService).sendWelcomeEmail(anyString(), any(Map.class));
		tmnProfileServiceImpl.setEmailService(emailService);

		TmnProfile tmnProfile =  tmnProfileServiceImpl.confirmCreateProfile(41, otp);

		assertNotNull(tmnProfile);
		verify(emailService).sendWelcomeEmail(anyString(), any(Map.class));
	}
}
