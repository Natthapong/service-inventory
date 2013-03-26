package th.co.truemoney.serviceinventory.sms;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;

public class OTPService {

	private static Logger logger = Logger.getLogger(OTPService.class);

	@Autowired
	private SmsProxy smsProxyImpl;

	@Autowired @Qualifier("smsSender")
	private String smsSender;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private OTPGenerator otpGenerator;

	public OTP send(String mobileNo) throws ServiceInventoryException {
		try {
			OTP otp = otpGenerator.generateNewOTP(mobileNo);

			logger.debug("==============================");
			logger.debug("mobileno = " + otp.getMobileNo());
			logger.debug("otp = " + otp.getOtpString());
			logger.debug("refCode = " + otp.getReferenceCode());
			logger.debug("==============================");

			SmsRequest smsRequest = new SmsRequest(smsSender, mobileNo,
					"รหัส OTP คือ " + otp.getOtpString() + " (Ref: " + otp.getReferenceCode() + ")");
			SmsResponse smsResponse = smsProxyImpl.send(smsRequest);
			if (!smsResponse.isSuccess()) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.SEND_OTP_FAIL, "send OTP failed.");
			}
			otpRepository.saveOTP(otp);

			return new OTP(otp.getMobileNo(), otp.getReferenceCode(), otp.getOtpString().replaceAll(".", "x"));

		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), e.getMessage(), e.getNamespace());
		}
	}

	public boolean isValidOTP(OTP inputOTP) throws ServiceInventoryException {

		if (inputOTP == null || inputOTP.getReferenceCode() == null) {
			return false;
		}

		OTP otp = otpRepository.getOTPByRefCode(inputOTP.getReferenceCode());

		return otp != null && otp.getOtpString().equals(inputOTP.getOtpString());

	}



}
