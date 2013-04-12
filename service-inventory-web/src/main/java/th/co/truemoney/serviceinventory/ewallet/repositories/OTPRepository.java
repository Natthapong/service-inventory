package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public interface OTPRepository {

	public void save(OTP otpBean);
	public OTP findOTPByRefCode(String mobileNumber, String refCode);

}
