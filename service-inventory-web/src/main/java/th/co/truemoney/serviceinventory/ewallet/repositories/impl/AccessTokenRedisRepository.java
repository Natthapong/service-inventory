package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessTokenRedisRepository implements AccessTokenRepository {

	private static Logger logger = Logger.getLogger(AccessTokenRedisRepository.class);

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void save(AccessToken token) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData(token.getAccessTokenID(), mapper.writeValueAsString(token), 15L);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public AccessToken getAccessToken(String accessTokenID) throws ServiceInventoryException {
		try {
			String result = redisLoggingDao.getData(accessTokenID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
						"access token not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, AccessToken.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public void remove(String accessTokenID) {
		try {
			redisLoggingDao.delete(accessTokenID);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not removed data in repository.");
		}
	}

	@Override
	public void extendAccessToken(String accessTokenID) {
		try {
			redisLoggingDao.setExpire(accessTokenID, 15L);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}		
	}
	
}
