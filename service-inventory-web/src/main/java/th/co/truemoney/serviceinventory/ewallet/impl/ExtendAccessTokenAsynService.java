package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;

@Service
public class ExtendAccessTokenAsynService {

	private static Logger logger = LoggerFactory.getLogger(ExtendAccessTokenAsynService.class);

	@Autowired
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
