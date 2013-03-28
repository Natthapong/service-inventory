package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ProfileRedisRepository implements ProfileRepository {

	private static Logger logger = LoggerFactory.getLogger(ProfileRedisRepository.class);

	@Autowired
	private RedisLoggingDao redisLoggingDao;
	
	@Override
	public void saveProfile(TmnProfile tmnProfile) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("profile:"+tmnProfile.getMobileNumber(), mapper.writeValueAsString(tmnProfile), 5L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public TmnProfile getTmnProfile(String mobileNumber) {
		try {
			String result = redisLoggingDao.getData("profile:"+mobileNumber);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.PROFILE_NOT_FOUND,
						"profile not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, TmnProfile.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
