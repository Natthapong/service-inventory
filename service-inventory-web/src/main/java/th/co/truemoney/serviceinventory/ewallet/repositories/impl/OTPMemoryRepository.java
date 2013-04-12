package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;

public class OTPMemoryRepository implements OTPRepository {

	public HashMap<String, OTP> otpsMap = new LinkedHashMap<String, OTP>();

	@Override
	public void save(OTP otp) {
		otpsMap.put(createKey(otp.getMobileNumber(), otp.getReferenceCode()), otp);
	}

	@Override
	public OTP findOTPByRefCode(String mobileNumber, String refCode) {
		return otpsMap.get(createKey(mobileNumber, refCode));
	}

	private String createKey(String mobileNumber, String referenceCode) {
		return mobileNumber + ":" + referenceCode;
	}

	public void clear() {
		this.otpsMap.clear();
	}
}
