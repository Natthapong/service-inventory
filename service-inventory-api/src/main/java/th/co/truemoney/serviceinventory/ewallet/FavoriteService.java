package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface FavoriteService {
	
	public Favorite addFavorite(Favorite favorite) throws ServiceInventoryException;
	
}
