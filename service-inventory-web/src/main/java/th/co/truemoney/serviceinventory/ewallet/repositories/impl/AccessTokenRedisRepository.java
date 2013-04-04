package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessTokenRedisRepository implements AccessTokenRepository {

	private static Logger logger = LoggerFactory.getLogger(AccessTokenRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void save(AccessToken token) {
		try {
			redisLoggingDao.addData(token.getAccessTokenID(), mapper.writeValueAsString(token), 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public AccessToken getAccessToken(String accessTokenID) throws ServiceInventoryException {
		try {
			String result = redisLoggingDao.getData(accessTokenID);
			if(result == null) {
				throw new ResourceNotFoundException(Code.ACCESS_TOKEN_NOT_FOUND, "access token not found.");
			}

			return mapper.readValue(result, AccessToken.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public void remove(String accessTokenID) {
		try {
			redisLoggingDao.delete(accessTokenID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not remove data in repository.", e);
		}
	}

	@Override
	public void extendAccessToken(String accessTokenID) {
		try {
			redisLoggingDao.setExpire(accessTokenID, 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

}
