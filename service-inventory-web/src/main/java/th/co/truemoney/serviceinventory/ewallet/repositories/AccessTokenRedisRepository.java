package th.co.truemoney.serviceinventory.ewallet.repositories;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

public class AccessTokenRedisRepository implements AccessTokenRepository {

	private static Logger logger = Logger.getLogger(AccessTokenRedisRepository.class);
	
	@Autowired
	private RedisLoggingDao redisLoggingDao;
		
	@Override
	public void save(AccessToken token) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData(token.getAccessTokenId(), mapper.writeValueAsString(token));
		} catch (Exception e) {
			logger.error(e);
		}
	}	

	@Override
	public AccessToken getAccessToken(String accessTokenId) {
		try {
			String result = redisLoggingDao.getData(accessTokenId);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, AccessToken.class);
		} catch (Exception e) {
			logger.error(e);			
		}
		return null;
	}

}
