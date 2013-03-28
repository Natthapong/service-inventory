package th.co.truemoney.serviceinventory.sms;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.util.RandomUtil;

public class UnSecureOTPGenerator implements OTPGenerator {

	@Override
	public OTP generateNewOTP(String mobileNumber) {
		return new OTP(mobileNumber, RandomUtil.genRandomString(4), "111111");
	}

}
