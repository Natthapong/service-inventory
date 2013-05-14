package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.LegacyFacade;

public class FavoriteServiceImpl implements FavoriteService {

	@Autowired
	private AccessTokenRepository accessTokenRepository;

	@Autowired
	private LegacyFacade legacyFacade;
	
	@Override
	public Favorite addFavorite(Favorite favorite, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withFavorite(favorite)
				.addFavorite();
	}

	@Override
	public List<Favorite> getFavorites(String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.getListFavorite();
	}
	
	public boolean isFavoritable(String serviceType, String serviceCode, String ref1, String accessTokenID){
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withServiceType(serviceType)
				.withServiceCode(serviceCode)
				.withRefernce1(ref1)
				.isFavoritable();
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
		this.accessTokenRepository = accessTokenRepository;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}
}
