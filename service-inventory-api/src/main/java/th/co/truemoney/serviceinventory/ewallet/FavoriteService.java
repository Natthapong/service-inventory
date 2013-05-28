package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import java.util.List;

public interface FavoriteService {
	public Favorite addFavorite(Favorite favorite, String accessTokenID) throws ServiceInventoryException;
    public List<Favorite> getFavorites(String accessTokenID) throws ServiceInventoryException;	
    public void deleteFavorite(String billCode,String ref1, String accessTokenID) throws ServiceInventoryException;
}
