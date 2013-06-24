package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedFavoritePaymentException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;

public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private LegacyFacade legacyFacade;

    @Override
    public Favorite addFavorite(Favorite favorite, String accessTokenID)
            throws ServiceInventoryException {
        AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);

        if(!isFavoritable(favorite.getServiceType() , favorite.getServiceCode(), favorite.getRef1(), accessToken.getAccessTokenID())) {
            throw new UnVerifiedFavoritePaymentException(Code.FAVORITE_SERVICE_CODE_NOT_INLIST, "service code not in list: " + favorite.getServiceCode());
        }

        return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
                .fromChannel(accessToken.getChannelID())
                .withFavorite(favorite)
                .addFavorite();
    }

    @Override
    public Boolean deleteFavorite(String serviceCode, String ref1,
            String accessTokenID) throws ServiceInventoryException {

        AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);

        return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
                .fromChannel(accessToken.getChannelID())
                .withServiceCode(serviceCode)
                .withRefernce1(ref1)
                .removeFavorite();
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
