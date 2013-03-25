package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;

@Service
public class ExtendAccessTokenAsynService {

	private static Logger logger = Logger.getLogger(ExtendAccessTokenAsynService.class);

	@Autowired @Qualifier("accessTokenRedisRepository")
	private AccessTokenRepository accessTokenRepo;
	
	@Async
	public Future<Boolean> setExpire(String accessTokenID) {
		try {
			accessTokenRepo.extendAccessToken(accessTokenID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new AsyncResult<Boolean> (true);
	}
	
}
