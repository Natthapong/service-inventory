package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
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
	private RedisLoggingDao redisLoggingDao;

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
		try {

			String result = redisLoggingDao.getData(createKey(mobileNumber, refCode));

			if(result == null) {
				throw new ResourceNotFoundException(Code.OTP_NOT_FOUND, "OTP not found.");
			}

			return mapper.readValue(result, OTP.class);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	private String createKey(String mobileNumber, String referenceCode) {
		return "otp:" + mobileNumber + ":" + referenceCode;
	}

}
