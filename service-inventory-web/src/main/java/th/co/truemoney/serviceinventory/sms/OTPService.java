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
	
	private static final String SMS_TEMPLATE = "รหัส OTP คือ %s (Ref: %s)";
	
	@Autowired 
	@Qualifier("smsSender")
	private String smsSender;

	@Autowired
	private SmsProxy smsProxyImpl;

	@Autowired
	private OTPGenerator otpGenerator;

	@Autowired
	private OTPRepository otpRepository;
	
	public OTP send(String mobileNumber) throws ServiceInventoryWebException {

		OTP otp = otpGenerator.generateNewOTP(mobileNumber);
		log(otp);
		
		if (sendSMS(mobileNumber, otp)) {
			otpRepository.save(otp);
			return new OTP(otp.getMobileNumber(), otp.getReferenceCode(), maskString(otp.getOtpString()));
		} else {
			throw new ServiceInventoryWebException(Code.SEND_OTP_FAIL, "send OTP failed.");
		}
	}

	public void isValidOTP(OTP inputOTP) throws ServiceInventoryWebException {
		if (inputOTP == null || inputOTP.getReferenceCode() == null) {
			throw new ServiceInventoryWebException(Code.INVALID_OTP, "invalid OTP.");
		}
		log(inputOTP);
		
		OTP otp = otpRepository.findOTPByRefCode(inputOTP.getMobileNumber(), inputOTP.getReferenceCode());
		
		if (otp != null) {
			log(otp);
			if (!otp.getOtpString().equals(inputOTP.getOtpString())) {
				throw new ServiceInventoryWebException(Code.OTP_NOT_MATCH, "OTP not matched.");
			}
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
		otp.setOtpString(otpString);

		log(otp);

		otpRepository.save(otp);
		return new OTP(otp.getMobileNumber(), otp.getReferenceCode(), otp.getOtpString().replaceAll(".", "x"));
	}
	
	private void log(OTP otp) {
		logger.debug("==============================");
		logger.debug("mobileNumber = " + otp.getMobileNumber());
		logger.debug("otp = " + otp.getOtpString());
		logger.debug("refCode = " + otp.getReferenceCode());
		logger.debug("==============================");
	}
	
	private String maskString(String str) {
		return str.replaceAll(".", "x");
	}
	
	private boolean sendSMS(String mobileNumber, OTP otp) {
		String msg = String.format(SMS_TEMPLATE, otp.getOtpString(), otp.getReferenceCode());
		SmsResponse smsResponse = smsProxyImpl.send(new SmsRequest(smsSender, mobileNumber, msg));
		return smsResponse.isSuccess();
	}
}
