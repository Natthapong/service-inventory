package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class AccessTokenRedisRepository implements AccessTokenRepository {

	private static Logger logger = Logger.getLogger(AccessTokenRedisRepository.class);
	
	@Autowired
	private RedisLoggingDao redisLoggingDao;
		
	@Override
	public void save(AccessToken token) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData(token.getAccessTokenID(), mapper.writeValueAsString(token), 10L);
		} catch (Exception e) {
			logger.error(e);
		}
	}	

	@Override
	public AccessToken getAccessToken(String accessTokenId) throws ServiceInventoryException {
		try {
			String result = redisLoggingDao.getData(accessTokenId);
			
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

}
