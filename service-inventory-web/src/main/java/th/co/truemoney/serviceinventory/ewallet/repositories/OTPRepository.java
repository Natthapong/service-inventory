package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bean.OTPBean;

public interface OTPRepository {

	public void saveOTP(OTPBean otpBean);
	public OTPBean getOTP(String mobileno);	

}
