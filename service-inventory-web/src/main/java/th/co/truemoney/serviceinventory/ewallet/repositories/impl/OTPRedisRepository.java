package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class OTPRedisRepository implements OTPRepository {

	private static Logger logger = LoggerFactory.getLogger(OTPRedisRepository.class);

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveOTP(OTP otp) {
		try {
			redisLoggingDao.addData(otp.getReferenceCode(), mapper.writeValueAsString(otp), 3L);
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR, "Can not stored data in repository.");
		}

	}

	@Override
	public OTP getOTPByRefCode(String refCode) {
		try {

			String result = redisLoggingDao.getData(refCode);

			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_FOUND, "OTP not found.");
			}

			return mapper.readValue(result, OTP.class);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	    return null;
	}

}
