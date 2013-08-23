package th.co.truemoney.serviceinventory.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.InvalidMobileNumberException;

public class OTPGeneratorTest {
	
	public static class UnsecureOTPGeneratorTest {
		
		OTPGenerator generator = new UnSecureOTPGenerator();
		
		@Test
		public void emptyMobileNumber() {
			try {
				generator.generateNewOTP("");
				fail("Exception not thrown for invalid mobile number");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidMobileNumberException);
			}
		}
		
		@Test
		public void nullMobileNumber() {
			try {
				generator.generateNewOTP(null);
				fail("Exception not thrown for invalid mobile number");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidMobileNumberException);
			}
		}
		
		@Test
		public void generateOTP() {
			String mobileNumber = "0891776544";
			OTP otp = generator.generateNewOTP(mobileNumber);
			assertEquals(mobileNumber, otp.getMobileNumber());
			assertEquals("111111", otp.getOtpString());
			assertEquals(4, otp.getReferenceCode().length());
		}
	}
	
	public static class RandomOTPGeneratorTest {
		
		OTPGenerator generator = new RandomOTPGeneraor();

		@Test
		public void emptyMobileNumber() {
			try {
				generator.generateNewOTP("");
				fail("Exception not thrown for invalid mobile number");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidMobileNumberException);
			}
		}
		
		@Test
		public void nullMobileNumber() {
			try {
				generator.generateNewOTP(null);
				fail("Exception not thrown for invalid mobile number");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidMobileNumberException);
			}
		}
		
		@Test
		public void generateOTP() {
			String mobileNumber = "0891776544";
			OTP otp = generator.generateNewOTP(mobileNumber);
			assertEquals(mobileNumber, otp.getMobileNumber());
			assertEquals(6, otp.getOtpString().length());
			assertEquals(4, otp.getReferenceCode().length());
		}
	}
	
}
