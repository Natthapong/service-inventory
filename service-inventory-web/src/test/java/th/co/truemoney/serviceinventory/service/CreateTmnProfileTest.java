package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.impl.TmnProfileAdminProxyImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.TmnProfileStubbed;

public class CreateTmnProfileTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;
	private TmnProfileAdminProxy tmnProfileAdminProxyMock;
	private OTPService otpServiceMock;
	private ProfileRepository profileRepositoryMock;

	@Before
	public void setup() {
		this.tmnProfileServiceImpl = new TmnProfileServiceImpl();
		this.tmnProfileAdminProxyMock = Mockito.mock(TmnProfileAdminProxyImpl.class);
		this.otpServiceMock = Mockito.mock(OTPService.class);
		this.profileRepositoryMock = Mockito.mock(ProfileRepository.class);

		this.tmnProfileServiceImpl.setTmnProfileAdminProxy(tmnProfileAdminProxyMock);
		this.tmnProfileServiceImpl.setOtpService(otpServiceMock);
		this.tmnProfileServiceImpl.setProfileRepository(profileRepositoryMock);
	}

	@Test
	public void createTmnProfileSuccess() {

		//given
		StandardBizResponse stubbedStandardBizResponse = TmnProfileStubbed.createSuccessStubbedStandardBizResponse();
		when(tmnProfileAdminProxyMock.isCreatable(Mockito.any(IsCreatableRequest.class))).thenReturn(stubbedStandardBizResponse);

		String stubbedOtpReferenceCode = "abcd";
		when(otpServiceMock.send(any(String.class))).thenReturn(new OTP("0861234567", stubbedOtpReferenceCode));

		doNothing().when(profileRepositoryMock).saveProfile(any(TmnProfile.class));
		
		//when
		Integer channelID = 40;
		TmnProfile tmnProfile = setTmnProfile();
		OTP otp = this.tmnProfileServiceImpl.createProfile(channelID, tmnProfile);

		//then
		assertEquals("abcd", otp.getReferenceCode());
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
		verify(otpServiceMock).send(any(String.class));
	}

	@Test
	public void createTmnProfileFailedValidateMobileno() {
		//given
		Exception ewalletException = TmnProfileStubbed.createFailedThrowEwalletException();
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class))).thenThrow(ewalletException);

		String stubbedOtpReferenceCode = "abcd";
		when(otpServiceMock.send(any(String.class))).thenReturn(new OTP("0861234567", stubbedOtpReferenceCode));

		//when
		try {
			Integer channelID = 40;
			TmnProfile tmnProfile = setTmnProfile();
			this.tmnProfileServiceImpl.createProfile(channelID, tmnProfile);
		} catch (ServiceInventoryException e) {
			assertEquals("error code", e.getCode());
			assertEquals("error namespace", e.getNamespace());
		}

		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
		verify(otpServiceMock, never()).send(any(String.class));
	}


	@Test
	public void createTmnProfileFailedSendOTP() {
		//given
		StandardBizResponse stubbedStandardBizResponse = TmnProfileStubbed.createSuccessStubbedStandardBizResponse();
		when(tmnProfileAdminProxyMock.isCreatable(any(IsCreatableRequest.class))).thenReturn(stubbedStandardBizResponse);

		Exception serviceInventoryException = TmnProfileStubbed.createFailedThrowServiceInventoryException();
		when(otpServiceMock.send(any(String.class))).thenThrow(serviceInventoryException);

		//when
		try {
			Integer channelID = 40;
			TmnProfile tmnProfile = setTmnProfile();
			this.tmnProfileServiceImpl.createProfile(channelID, tmnProfile);
		} catch (ServiceInventoryException e) {
			assertEquals("error code", e.getCode());
			assertEquals("error description", e.getDescription());
			assertEquals("error namespace", e.getNamespace());
		}

		//then
		verify(tmnProfileAdminProxyMock).isCreatable(any(IsCreatableRequest.class));
		verify(otpServiceMock).send(any(String.class));
	}

	private TmnProfile setTmnProfile() {
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setEmail("test@gmail.com");
		tmnProfile.setPassword("123456");
		tmnProfile.setFullname("UnitTest");
		tmnProfile.setThaiID("1212121212121");
		tmnProfile.setMobileno("0891234567");
		return tmnProfile;
	}

}
