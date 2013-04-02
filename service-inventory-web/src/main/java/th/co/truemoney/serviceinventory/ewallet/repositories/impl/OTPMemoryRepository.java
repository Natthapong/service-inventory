package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;

public class OTPMemoryRepository implements OTPRepository {

	public static HashMap<String, OTP> otpsMap = new LinkedHashMap<String, OTP>();

	@Override
	public void saveOTP(OTP otp) {
		otpsMap.put(createKey(otp.getMobileNumber(), otp.getReferenceCode()), otp);
	}

	@Override
	public OTP getOTPByRefCode(String mobileNumber, String refCode) {
		return otpsMap.get(createKey(mobileNumber, refCode));
	}

	private String createKey(String mobileNumber, String referenceCode) {
		return mobileNumber + ":" + referenceCode;
	}
}
