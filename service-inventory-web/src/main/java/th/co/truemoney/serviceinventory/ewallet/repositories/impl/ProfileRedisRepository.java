package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class ProfileRedisRepository implements RegisteringProfileRepository {

	private static Logger logger = LoggerFactory.getLogger(ProfileRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ExpirableMap redisLoggingDao;

	@Override
	public void saveRegisteringProfile(TmnProfile tmnProfile) {
		try {
			redisLoggingDao.addData("profile:" + tmnProfile.getMobileNumber(), mapper.writeValueAsString(tmnProfile), 20L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public TmnProfile findRegisteringProfileByMobileNumber(String mobileNumber) {
		try {
			String result = redisLoggingDao.getData("profile:"+mobileNumber);
			if(result == null) {
				throw new ResourceNotFoundException(Code.PROFILE_NOT_FOUND,
						"profile not found.");
			}

			return mapper.readValue(result, TmnProfile.class);

		} catch (ServiceInventoryWebException e) {
			throw e;
		} catch (Exception e) {

			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

}
