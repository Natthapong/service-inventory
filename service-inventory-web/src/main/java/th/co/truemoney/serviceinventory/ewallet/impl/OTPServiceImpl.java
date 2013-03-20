package th.co.truemoney.serviceinventory.ewallet.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.ewallet.OTPService;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.util.RandomUtil;

public class OTPServiceImpl implements OTPService {
	
	private static Logger logger = Logger.getLogger(OTPServiceImpl.class);
	
	@Autowired
	private SmsProxy smsProxyImpl;
	
	@Autowired @Qualifier("smsSender")
	private String smsSender;

	@Autowired @Qualifier("otpRedisRepository")
	private OTPRepository otpRepository;
	
	@Override
	public String send(String mobileno) throws ServiceInventoryException {
		try {
			OTPBean otpBean = new OTPBean(mobileno, RandomUtil.genRandomNumber(6), RandomUtil.genRandomString(4));
			logger.debug("==============================");
			logger.debug("mobileno = "+otpBean.getMobileno());
			logger.debug("otp = "+otpBean.getOtpString());
			logger.debug("refCode = "+otpBean.getOtpReferenceCode());
			logger.debug("==============================");
			SmsRequest smsRequest = new SmsRequest(smsSender, mobileno, 
					"Your OTP is "+otpBean.getOtpString()+" (Reference Code = "+otpBean.getOtpReferenceCode()+").");
			smsProxyImpl.send(smsRequest);
			otpRepository.saveOTP(otpBean);
			return otpBean.getOtpReferenceCode();
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.SEND_OTP_FAIL, "send OTP failed.");			
		} 
	}

	@Override
	public String getOTPString(String mobileno) throws ServiceInventoryException {
			OTPBean otpBean = otpRepository.getOTP(mobileno);
			if(otpBean == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_FOUND, "OTP not found. ");
			}
		return otpBean.getOtpString();
	}
	
	
	
}
