package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;

public class OTPMemoryRepository implements OTPRepository {

	public static HashMap<String, OTP> otpsMap = new LinkedHashMap<String, OTP>();

	@Override
	public void saveOTP(OTP otp) {
		otpsMap.put(otp.getReferenceCode(), otp);
	}

	@Override
	public OTP getOTPByRefCode(String refCode) {
		return otpsMap.get(refCode);
	}

}
