package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class FavoriteServiceImpl implements FavoriteService {

	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	@Override
	public Favorite addFavorite(Favorite favorite, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		favorite.setFavoriteID(1000l);
		return favorite;
	}

	@Override
	public List<Favorite> getFavorites(String serviceType, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		return new ArrayList<Favorite>();
	}

}
