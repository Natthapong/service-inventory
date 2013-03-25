package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface AccessTokenRepository {
	
	public void save(AccessToken token);
	public AccessToken getAccessToken(String accessTokenID) throws ServiceInventoryException ;
	public void remove(String accessTokenID);
	public void extendAccessToken(String accessTokenID);
	
}
