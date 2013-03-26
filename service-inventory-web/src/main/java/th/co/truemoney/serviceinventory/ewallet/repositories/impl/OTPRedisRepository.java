package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bean.OTPBean;
import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OTPRedisRepository implements OTPRepository {

	private static Logger logger = LoggerFactory.getLogger(OTPRedisRepository.class);

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveOTP(OTPBean otpBean) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData(otpBean.getMobileno(), mapper.writeValueAsString(otpBean), 3L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public OTPBean getOTP(String mobileno) {
		try {
			String result = redisLoggingDao.getData(mobileno);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.OTP_NOT_FOUND,
						"otp not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, OTPBean.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
