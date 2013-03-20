package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;

public class AccessTokenMemoryRepository implements AccessTokenRepository {
	
	private static Logger logger = Logger.getLogger(AccessTokenMemoryRepository.class);
	
	public static HashMap<String, AccessToken> map = new LinkedHashMap<String, AccessToken>();

	@Override
	public void save(AccessToken token) {
		map.put(token.getAccessTokenID(), token);
	}
	
	@Override
	public AccessToken getAccessToken(String accessTokenID) {
		return map.get(accessTokenID);
	}
	
	@Override
	public void remove(String accessTokenID) {
		map.remove(accessTokenID);
	}

	public void dump() {
	  Collection<AccessToken> collection = map.values();
	  for (Iterator<AccessToken> iterator = collection.iterator(); iterator.hasNext();) {
		AccessToken accessToken = (AccessToken) iterator.next();
		logger.debug("accessToken: "+accessToken.toString());
	  }
	}
}
