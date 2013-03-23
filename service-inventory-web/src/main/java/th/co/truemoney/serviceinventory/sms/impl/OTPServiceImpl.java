package th.co.truemoney.serviceinventory.sms.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.sms.OTPService;
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
					"รหัส OTP คือ "+otpBean.getOtpString()+" (Ref: "+otpBean.getOtpReferenceCode()+")");
			SmsResponse smsResponse = smsProxyImpl.send(smsRequest);
			if (!smsResponse.isSuccess()) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.SEND_OTP_FAIL, "send OTP failed."); 
			}
			otpRepository.saveOTP(otpBean);
			return otpBean.getOtpReferenceCode();
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
					e.getMessage(), e.getNamespace());		
		} 
	}

	@Override
	public String getOTPString(String mobileno) throws ServiceInventoryException {
		OTPBean otpBean = otpRepository.getOTP(mobileno);
		return otpBean.getOtpString();
	}
	
	
	
}
