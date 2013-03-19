package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;

public class OTPMemoryRepository implements OTPRepository {

	public static HashMap<String, OTPBean> mapOTP = new LinkedHashMap<String, OTPBean>();

	@Override
	public void saveOTP(OTPBean otpBean) {
		mapOTP.put(otpBean.getMobileno(), otpBean);		
	}

	@Override
	public OTPBean getOTP(String mobileno) {
		return mapOTP.get(mobileno);
	}

}
