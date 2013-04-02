package th.co.truemoney.serviceinventory.sms;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(OTPService.class);

	@Autowired
	private SmsProxy smsProxyImpl;

	@Autowired @Qualifier("smsSender")
	private String smsSender;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private OTPGenerator otpGenerator;

	public OTP send(String mobileNumber) throws ServiceInventoryException {
		try {
			OTP otp = otpGenerator.generateNewOTP(mobileNumber);

			logger.debug("==============================");
			logger.debug("mobileNumber = " + otp.getMobileNumber());
			logger.debug("otp = " + otp.getOtpString());
			logger.debug("refCode = " + otp.getReferenceCode());
			logger.debug("==============================");

			SmsRequest smsRequest = new SmsRequest(smsSender, mobileNumber,
					"รหัส OTP คือ " + otp.getOtpString() + " (Ref: " + otp.getReferenceCode() + ")");
			SmsResponse smsResponse = smsProxyImpl.send(smsRequest);
			if (!smsResponse.isSuccess()) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.SEND_OTP_FAIL, "send OTP failed.");
			}
			otpRepository.saveOTP(otp);

			return new OTP(otp.getMobileNumber(), otp.getReferenceCode(), otp.getOtpString().replaceAll(".", "x"));

		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), e.getMessage(), e.getNamespace());
		}
	}

	public boolean isValidOTP(OTP inputOTP) throws ServiceInventoryException {
		if (inputOTP == null || inputOTP.getReferenceCode() == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.INVALID_OTP, "invalid OTP.");
		}
		
		OTP otp = otpRepository.getOTPByRefCode(inputOTP.getReferenceCode());
		
		if (otp != null && !otp.getOtpString().equals(inputOTP.getOtpString())) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_MATCH, "OTP not matched.");
		}
		return true;
	}

	public void setOTPRepository(OTPRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	public void setOTPGenerator(OTPGenerator otpGenerator) {
		this.otpGenerator = otpGenerator;
	}

}
