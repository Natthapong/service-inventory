package th.co.truemoney.serviceinventory.legacyfacade.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddFavoriteRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.FavoriteContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsFavoritableRequest;
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

public class ProfileFacade {

	private static final int VALID_CUSTOMER_STATUS = 3;

	private static final String CUSTOMER_TYPE = "C";

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

		List<Favorite> list = new ArrayList<Favorite>();
		for(FavoriteContext context : favoriteContext){
			Favorite favorite = new Favorite();
			favorite.setAmount(context.getAmount());
			favorite.setFavoriteID(new Long(context.getFavoriteId()));
			favorite.setRef1(context.getReference1());
			favorite.setServiceCode(context.getServiceCode());
			favorite.setServiceType(context.getServiceType());
			list.add(favorite);
		}
		return list;
	}

	public Boolean isFavorite(Integer channelID, String sessionID,
			String tmnID, String serviceType, String serviceCode,
			String reference1) {
		
		StandardBizResponse  standardBizResponse =  this.tmnProfileProxy.isFavoritable(createIsFavoritableRequest(
				channelID,sessionID,tmnID,serviceType,serviceCode,reference1));
		return "0".equals(standardBizResponse.getResultCode());
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
		
		StandardBizResponse standardBizResponse = this.tmnProfileProxy.addFavorite(addFavoriteRequest);
		//will add favorite id later
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
