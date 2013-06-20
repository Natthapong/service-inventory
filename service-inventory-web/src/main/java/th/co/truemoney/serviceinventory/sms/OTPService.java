package th.co.truemoney.serviceinventory.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
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

	public OTP send(String mobileNumber) throws ServiceInventoryWebException {

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
			throw new ServiceInventoryWebException(Code.SEND_OTP_FAIL, "send OTP failed.");
		}
		otpRepository.save(otp);

		return new OTP(otp.getMobileNumber(), otp.getReferenceCode(), otp.getOtpString().replaceAll(".", "x"));

	}

	public void isValidOTP(OTP inputOTP) throws ServiceInventoryWebException {
		if (inputOTP == null || inputOTP.getReferenceCode() == null) {
			throw new ServiceInventoryWebException(Code.INVALID_OTP, "invalid OTP.");
		}

		OTP otp = otpRepository.findOTPByRefCode(inputOTP.getMobileNumber(), inputOTP.getReferenceCode());

		if (otp != null && !otp.getOtpString().equals(inputOTP.getOtpString())) {
			throw new ServiceInventoryWebException(Code.OTP_NOT_MATCH, "OTP not matched.");
		}
	}

	public void setOTPRepository(OTPRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	public void setOTPGenerator(OTPGenerator otpGenerator) {
		this.otpGenerator = otpGenerator;
	}

	public OTP saveOtpString(String mobileNumber, String otpString) throws ServiceInventoryWebException {

		OTP otp = otpGenerator.generateNewOTP(mobileNumber);

		logger.debug("==============================");
		logger.debug("mobileNumber = " + otp.getMobileNumber());
		logger.debug("otp = " + otpString);
		logger.debug("refCode = " + otp.getReferenceCode());
		logger.debug("==============================");

		otpRepository.save(otp);

		return new OTP(otp.getMobileNumber(), otp.getReferenceCode(), otpString.replaceAll(".", "x"));

	}

}
