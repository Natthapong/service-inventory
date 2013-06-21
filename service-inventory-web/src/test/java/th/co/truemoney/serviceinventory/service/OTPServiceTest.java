package th.co.truemoney.serviceinventory.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@Category(IntegrationTest.class)
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
        when(otpRepositoryMock.findOTPByRefCode(anyString(), anyString())).thenThrow(new ServiceInventoryWebException(Code.OTP_NOT_FOUND, "OTP not found."));

        //when
        try {
            OTP otp = new OTP("0866013468", "abcd", "111111");
            otpService.isValidOTP(otp);
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            Assert.assertEquals(Code.OTP_NOT_FOUND, e.getErrorCode());
            Assert.assertEquals("OTP not found.", e.getErrorDescription());
        }
        //then
        verify(otpRepositoryMock).findOTPByRefCode(anyString(), anyString());
    }

    @Test
    public void shouldOTPNotMatched() {
        //given
        OTP stubbedOTP = new OTP("0866013468", "defg", "222222");
        when(otpRepositoryMock.findOTPByRefCode(anyString(), anyString())).thenReturn(stubbedOTP);

        //when
        try {
            OTP otp = new OTP("0866013468", "abcd", "111111");
            otpService.isValidOTP(otp);
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            Assert.assertEquals(Code.OTP_NOT_MATCH, e.getErrorCode());
            Assert.assertEquals("OTP not matched.", e.getErrorDescription());
        }
        //then
        verify(otpRepositoryMock).findOTPByRefCode(anyString(), anyString());
    }

    @Test
    public void shouldInvalidOTP() {
        //given
        when(otpRepositoryMock.findOTPByRefCode(anyString(), anyString())).thenReturn(null);

        //when
        try {
            otpService.isValidOTP(null);
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            Assert.assertEquals(Code.INVALID_OTP, e.getErrorCode());
            Assert.assertEquals("invalid OTP.", e.getErrorDescription());
        }
        //then
        verify(otpRepositoryMock, never()).findOTPByRefCode(anyString(), anyString());
    }

    @Test
    public void shouldOTPMatched() {
        //given
        OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
        when(otpRepositoryMock.findOTPByRefCode(anyString(), anyString())).thenReturn(stubbedOTP);

        //when
        OTP otp = new OTP("0866013468", "abcd", "111111");
        otpService.isValidOTP(otp);

        //then
        verify(otpRepositoryMock).findOTPByRefCode(anyString(), anyString());
    }

}
