package th.co.truemoney.serviceinventory.sms;

import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.InvalidMobileNumberException;
import th.co.truemoney.serviceinventory.util.RandomUtil;

public class RandomOTPGeneraor implements OTPGenerator {

	@Override
	public OTP generateNewOTP(String mobileNumber) {
		if (StringUtils.hasText(mobileNumber)) {
			String refCode = RandomUtil.genRandomString(4);
			String otpCode = RandomUtil.genRandomNumber(6);
			return new OTP(mobileNumber, refCode, otpCode);
		}
		throw new InvalidMobileNumberException("Invalid mobile number: '" + mobileNumber + "'");
	}

}
