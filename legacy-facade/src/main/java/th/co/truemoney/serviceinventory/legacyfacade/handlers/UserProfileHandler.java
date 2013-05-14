package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritedRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListFavoriteResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class UserProfileHandler {

	private static final int VALID_CUSTOMER_STATUS = 3;
	
	private static final String SUCCESS_CODE = "0";

	private static final String CUSTOMER_TYPE = "C";
	
	private static final String ALREADY_ADD_FAVORITE = "2012";
	
	private static final String ADD_FAVORITE_DENIED  = "2013";

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	public AccessToken login(Integer channelID, String credentialUsername,String credentialSecret) {


		SignonResponse signon = this.tmnSecurityProxy.signon(createGetSignOnRequest(channelID, credentialUsername, credentialSecret));

		String sessionID = signon.getSessionId();
		String truemoneyID = signon.getTmnId();

		GetBasicProfileResponse profile = this.tmnProfileProxy.getBasicProfile(createAccessRequest(channelID, sessionID, truemoneyID));

		if (profile == null) {
			throw new ProfileNotFoundException();
		}

		if (!CUSTOMER_TYPE.equals(profile.getProfileType())) {
			throw new InvalidProfileTypeSignonException();

		} else if (VALID_CUSTOMER_STATUS != profile.getStatusId()) {
			throw new InvalidProfileStatusSignonException(profile.getStatusId());
		}

		return new AccessToken(
				UUID.randomUUID().toString(),
				signon.getSessionId(),
				signon.getTmnId(),
				profile.getMobile(),
				profile.getEmail(),
				channelID);
	}

	public TmnProfile getProfile(Integer channelID, String sessionID, String truemoneyID) {

		GetBasicProfileResponse profile = this.tmnProfileProxy.getBasicProfile(createAccessRequest(channelID, sessionID, truemoneyID));

		TmnProfile tmnProfile = new TmnProfile(profile.getFullName(), profile.getEwalletBalance());

		tmnProfile.setMobileNumber(profile.getMobile());
		tmnProfile.setEmail(profile.getEmail());
		tmnProfile.setType(profile.getProfileType());
		tmnProfile.setStatus(profile.getStatusId());

		return tmnProfile;
	}
	
	public List<Favorite> getListFavorite(Integer channelID, String sessionID,
			String tmnID, String serviceType) {
		SecurityContext securityContext = new SecurityContext(sessionID, tmnID);
		
		ListFavoriteRequest listFavoriteRequest = new ListFavoriteRequest();
		listFavoriteRequest.setChannelId(channelID);
		listFavoriteRequest.setServiceType(serviceType);
		listFavoriteRequest.setSecurityContext(securityContext);
		
		ListFavoriteResponse listFavoriteResponse = this.tmnProfileProxy.listFavorite(listFavoriteRequest);
		FavoriteContext[] favoriteContext = listFavoriteResponse.getFavoriteList();
		List<Favorite> favorites = createFavorites(favoriteContext);		
		
		return favorites;
	}

	public Boolean isFavoritable(Integer channelID, String sessionID,
			String tmnID, String serviceType, String serviceCode,
			String reference1) {
	
		try {
			StandardBizResponse  standardBizResponse =  this.tmnProfileProxy.isFavoritable(createIsFavoritableRequest(
					channelID,sessionID,tmnID,serviceType,serviceCode,reference1));
			return SUCCESS_CODE.equals(standardBizResponse.getResultCode());
		} catch (FailResultCodeException e) {
			if (ALREADY_ADD_FAVORITE.equals(e.getCode()) || ADD_FAVORITE_DENIED .equals(e.getCode())) {
				return false;
			} 
			throw e;			
		}		
	}
	
	public Boolean isFavorited(Integer channelID, String sessionID,
			String tmnID, String serviceType, String serviceCode,
			String reference1) {
		
		StandardBizResponse  standardBizResponse =  this.tmnProfileProxy.isFavorited(createIsFavoritedRequest(
				channelID,sessionID,tmnID,serviceType,serviceCode,reference1));
		return SUCCESS_CODE.equals(standardBizResponse.getResultCode());
	}

	public Favorite addFavorite(Integer channelID, String sessionID,
			String tmnID, Favorite favorite) {
		SecurityContext securityContext = new SecurityContext(sessionID, tmnID);
		
		AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest();
		addFavoriteRequest.setAmount(favorite.getAmount());
		addFavoriteRequest.setChannelId(channelID);
		addFavoriteRequest.setReference1(favorite.getRef1());
		addFavoriteRequest.setServiceCode(favorite.getServiceCode());
		addFavoriteRequest.setServiceType(favorite.getServiceType());
		addFavoriteRequest.setSecurityContext(securityContext);
		
		AddFavoriteResponse addFavoriteResponse = this.tmnProfileProxy.addFavorite(addFavoriteRequest);
		Long favoriteID = new Long(addFavoriteResponse.getFavorite().getFavoriteId());
		favorite.setFavoriteID(favoriteID);
		return favorite;
	}
	
	public void logout(Integer channelID, String sessionID, String truemoneyID) {
		this.tmnSecurityProxy.terminateSession(createAccessRequest(channelID, sessionID, truemoneyID));
	}
	
	private IsFavoritableRequest createIsFavoritableRequest(Integer channelID, String sessionID,
			String tmnID, String serviceType, String serviceCode,
			String reference1){
		SecurityContext securityContext = new SecurityContext(sessionID, tmnID);
		
		IsFavoritableRequest favoritableRequest = new IsFavoritableRequest();
		favoritableRequest.setChannelId(channelID);
		favoritableRequest.setServiceType(serviceType);
		favoritableRequest.setServiceCode(serviceCode);
		favoritableRequest.setReference1(reference1);
		favoritableRequest.setSecurityContext(securityContext);
		
		return favoritableRequest;
	}
	
	private IsFavoritedRequest createIsFavoritedRequest(Integer channelID, String sessionID,
			String tmnID, String serviceType, String serviceCode,
			String reference1){
		SecurityContext securityContext = new SecurityContext(sessionID, tmnID);
		
		IsFavoritedRequest favoritedRequest = new IsFavoritedRequest();
		favoritedRequest.setChannelId(channelID);
		favoritedRequest.setServiceType(serviceType);
		favoritedRequest.setServiceCode(serviceCode);
		favoritedRequest.setReference1(reference1);
		favoritedRequest.setSecurityContext(securityContext);
		
		return favoritedRequest;
	}
	
	private StandardBizRequest createAccessRequest(Integer channelID, String sessionID, String truemoneyID) {
		SecurityContext securityContext = new SecurityContext(sessionID, truemoneyID);

		StandardBizRequest standardBizRequest = new StandardBizRequest();
		standardBizRequest.setSecurityContext(securityContext);
		standardBizRequest.setChannelId(channelID);

		return standardBizRequest;
	}

	private SignonRequest createGetSignOnRequest(Integer channelID, String username, String password) {
		SignonRequest signonRequest = new SignonRequest();
		signonRequest.setInitiator(username);
		signonRequest.setPin(password);
		signonRequest.setChannelId(channelID);

		return signonRequest;
	}
	
	private List<Favorite> createFavorites(FavoriteContext[] favoriteContext) {
		List<Favorite> list = new ArrayList<Favorite>();
		
		if(favoriteContext!=null) {
			for(FavoriteContext context : favoriteContext){
				Favorite favorite = new Favorite();
				favorite.setAmount(context.getAmount());
				favorite.setFavoriteID(new Long(context.getFavoriteId()));
				favorite.setRef1(context.getReference1());
				favorite.setServiceCode(context.getServiceCode());
				favorite.setServiceType(context.getServiceType());
				list.add(favorite);
			}
		}
		
		return list;
	}

	public static class ProfileNotFoundException extends ServiceInventoryException {
		private static final long serialVersionUID = 7328535407875381185L;

		public ProfileNotFoundException() {
			super(500, "1008", "Profile not found", "TMN-SERVICE-INVENTORY");
		}
	}

	public static class InvalidProfileTypeSignonException extends ServiceInventoryException {
		private static final long serialVersionUID = 7328535407875381185L;

		public InvalidProfileTypeSignonException() {
			super(400, "10000", "Invalid profile type, is not a customer.", "TMN-SERVICE-INVENTORY");
		}
	}

	public static class InvalidProfileStatusSignonException extends ServiceInventoryException {
		private static final long serialVersionUID = 7328535407875381185L;

		public InvalidProfileStatusSignonException(int status) {
			super(400, "10002", "Invalid profile status: " + status, "TMN-SERVICE-INVENTORY");
		}
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}

}
