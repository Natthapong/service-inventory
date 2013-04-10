package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class AccessTokenMemoryRepository implements AccessTokenRepository {

	private static Logger logger = LoggerFactory.getLogger(AccessTokenMemoryRepository.class);

	public static HashMap<String, AccessToken> map = new LinkedHashMap<String, AccessToken>();

	@Override
	public void save(AccessToken token) {
		map.put(token.getAccessTokenID(), token);
	}

	@Override
	public AccessToken getAccessToken(String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = map.get(accessTokenID);
		if(accessToken == null) {
			throw new ResourceNotFoundException(Code.ACCESS_TOKEN_NOT_FOUND, "access token not found.");
		}
		return accessToken;
	}

	@Override
	public void remove(String accessTokenID) {
		map.remove(accessTokenID);
	}

	public void dump() {
	  Collection<AccessToken> collection = map.values();
	  for (Iterator<AccessToken> iterator = collection.iterator(); iterator.hasNext();) {
		AccessToken accessToken = (AccessToken) iterator.next();
		logger.debug("accessToken: " + accessToken.toString());
	  }
	}

	@Override
	public void extendAccessToken(String accessTokenID) {
		AccessToken accessToken = map.get(accessTokenID);

		if (accessToken != null) {
			logger.debug("access token extended: " + accessTokenID);
		} else {
			logger.debug("fail to extend access token: " + accessTokenID);
		}
	}

	public void clear() {
		map.clear();
	}

}
