package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public interface OTPRepository {

	public void saveOTP(OTP otpBean);
	public OTP getOTPByRefCode(String mobileNumber, String refCode);

}
