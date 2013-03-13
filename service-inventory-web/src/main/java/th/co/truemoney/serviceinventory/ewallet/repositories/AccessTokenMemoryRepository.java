package th.co.truemoney.serviceinventory.ewallet.repositories;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

@Component
public class AccessTokenMemoryRepository implements AccessTokenRepository {
	
	public static HashMap<String, AccessToken> map = new LinkedHashMap<String, AccessToken>();

	@Override
	public void save(AccessToken token) {
		map.put(token.getAccessTokenId(), token);
	}

}
