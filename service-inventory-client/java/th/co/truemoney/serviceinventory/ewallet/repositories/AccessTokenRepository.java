package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

public interface AccessTokenRepository {
	
	public void save(AccessToken token);
	public AccessToken getAccessToken(String accessTokenId);
	
}
