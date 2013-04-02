package th.co.truemoney.serviceinventory.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class OTPServiceTest {

	private OTPService otpService;

	private OTPRepository otpRepositoryMock;

	@Before
	public void setup() {
		this.otpService = new OTPService();
		this.otpRepositoryMock = Mockito.mock(OTPRepository.class);
		this.otpService.setOTPRepository(otpRepositoryMock);
	}

	@Test
	public void shouldOTPNotFound() {
		//given
		when(otpRepositoryMock.getOTPByRefCode(anyString(), anyString())).thenThrow(new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_FOUND, "OTP not found."));

		//when
		try {
			OTP otp = new OTP("0866013468", "abcd", "111111");
			otpService.isValidOTP(otp);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			Assert.assertEquals(ServiceInventoryException.Code.OTP_NOT_FOUND, e.getCode());
			Assert.assertEquals("OTP not found.", e.getDescription());
		}
		//then
		verify(otpRepositoryMock).getOTPByRefCode(anyString(), anyString());
	}

	@Test
	public void shouldOTPNotMatched() {
		//given
		OTP stubbedOTP = new OTP("0866013468", "defg", "222222");
		when(otpRepositoryMock.getOTPByRefCode(anyString(), anyString())).thenReturn(stubbedOTP);

		//when
		try {
			OTP otp = new OTP("0866013468", "abcd", "111111");
			otpService.isValidOTP(otp);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			Assert.assertEquals(ServiceInventoryException.Code.OTP_NOT_MATCH, e.getCode());
			Assert.assertEquals("OTP not matched.", e.getDescription());
		}
		//then
		verify(otpRepositoryMock).getOTPByRefCode(anyString(), anyString());
	}

	@Test
	public void shouldInvalidOTP() {
		//given
		when(otpRepositoryMock.getOTPByRefCode(anyString(), anyString())).thenReturn(null);

		//when
		try {
			otpService.isValidOTP(null);
			Assert.fail();
		} catch (ServiceInventoryException e) {
			Assert.assertEquals(ServiceInventoryException.Code.INVALID_OTP, e.getCode());
			Assert.assertEquals("invalid OTP.", e.getDescription());
		}
		//then
		verify(otpRepositoryMock, never()).getOTPByRefCode(anyString(), anyString());
	}

	@Test
	public void shouldOTPMatched() {
		//given
		OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
		when(otpRepositoryMock.getOTPByRefCode(anyString(), anyString())).thenReturn(stubbedOTP);

		//when
		OTP otp = new OTP("0866013468", "abcd", "111111");
		otpService.isValidOTP(otp);

		//then
		verify(otpRepositoryMock).getOTPByRefCode(anyString(), anyString());
	}

}
