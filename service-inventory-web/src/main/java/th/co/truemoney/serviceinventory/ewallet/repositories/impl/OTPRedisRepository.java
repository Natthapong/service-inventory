package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OTPRedisRepository implements OTPRepository {

	private static Logger logger = LoggerFactory.getLogger(OTPRedisRepository.class);

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ExpirableMap redisLoggingDao;

	@Override
	public void save(OTP otp) {
		try {
			redisLoggingDao.addData(createKey(otp.getMobileNumber(), otp.getReferenceCode()), mapper.writeValueAsString(otp), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}

	}

	@Override
	public OTP findOTPByRefCode(String mobileNumber, String refCode) {
		OTP otp = null;
		
		try {

			String result = redisLoggingDao.getData(createKey(mobileNumber, refCode));

			otp = mapper.readValue(result, OTP.class);
			
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
		
		if(otp == null) {
			throw new ResourceNotFoundException(Code.OTP_NOT_FOUND, "OTP not found.");
		}

		logger.debug("=======================================");
		logger.debug("Mobile Number : "+otp.getMobileNumber());
		logger.debug("Ref Code : "+otp.getReferenceCode());
		logger.debug("=======================================");

		return otp;

	}

	private String createKey(String mobileNumber, String referenceCode) {
		return "otp:" + mobileNumber + ":" + referenceCode;
	}

}
