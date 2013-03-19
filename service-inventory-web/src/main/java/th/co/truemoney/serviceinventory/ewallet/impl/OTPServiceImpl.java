package th.co.truemoney.serviceinventory.ewallet.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.OTPService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.util.RandomUtil;

public class OTPServiceImpl implements OTPService {

	public static final String OTP_MESSAGE = "Your otp is ";
	
	private static Logger logger = Logger.getLogger(OTPServiceImpl.class);
	
	@Autowired
	private SmsProxy smsProxyImpl;
	
	@Autowired @Qualifier("smsSender")
	private String smsSender;

	@Autowired 
	private OrderRepository orderRepository;
	
	@Override
	public OTP send(String mobileno) throws ServiceInventoryException {
		try {
			OTP otp = new OTP(mobileno, RandomUtil.genRandomNumber(6), RandomUtil.genRandomString(4));
			logger.debug("==============================");
			logger.debug("mobileno = "+otp.getMobileno());
			logger.debug("otp = "+otp.getOtpString());
			logger.debug("refCode = "+otp.getOtpReferenceCode());
			logger.debug("==============================");
			SmsRequest smsRequest = new SmsRequest(smsSender, mobileno, OTP_MESSAGE+otp.getOtpString());
			SmsResponse smsResponse = smsProxyImpl.send(smsRequest);
			logger.debug("smsResponse: "+smsResponse.toString());
			return otp;
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.SEND_OTP_FAIL, "send OTP failed.");			
		} 
	}
	
}
