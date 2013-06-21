package th.co.truemoney.serviceinventory.sms;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public interface OTPGenerator {
	OTP generateNewOTP(String mobileNumber);
}
