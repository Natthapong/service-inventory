package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.tmn.core.api.message.AddFavoriteRequest;
import com.tmn.core.api.message.AddFavoriteResponse;
import com.tmn.core.api.message.ChangePasswordRequest;
import com.tmn.core.api.message.ChangePinRequest;
import com.tmn.core.api.message.DeleteFavoriteRequest;
import com.tmn.core.api.message.FavoriteContext;
import com.tmn.core.api.message.GetProfileRequest;
import com.tmn.core.api.message.GetProfileResponse;
import com.tmn.core.api.message.IsFavoritableRequest;
import com.tmn.core.api.message.IsFavoritedRequest;
import com.tmn.core.api.message.ListFavoriteRequest;
import com.tmn.core.api.message.ListFavoriteResponse;
import com.tmn.core.api.message.ProfileKey;
import com.tmn.core.api.message.SecurityContext;
import com.tmn.core.api.message.SignonRequest;
import com.tmn.core.api.message.SignonResponse;
import com.tmn.core.api.message.StandardBizResponse;
import com.tmn.core.api.message.UpdateProfileRequest;

public class UserProfileHandler {

    private static final String SUCCESS_CODE = "0";

    private Logger logger = Logger.getLogger(UserProfileHandler.class);
    
    @Autowired
    private TmnSecurityProxyClient tmnSecurityProxyClient;

    @Autowired
    private TmnProfileProxyClient tmnProfileProxyClient;
    
    public AccessToken login(Integer channelID, String credentialUsername,String credentialSecret) {
        SignonResponse signon = this.tmnSecurityProxyClient.signon(createGetSignOnRequest(channelID, credentialUsername, credentialSecret));

        String sessionID = signon.getSessionId();
        String truemoneyID = signon.getTmnId();

        GetProfileResponse profile = this.tmnProfileProxyClient.getProfile(createGetProfileRequest(channelID, sessionID, truemoneyID));

        if (profile == null) {
            throw new ProfileNotFoundException();
        }
        
        return new AccessToken(
			        UUID.randomUUID().toString(),
			        credentialUsername,
			        signon.getSessionId(),
			        signon.getTmnId(),
			        profile.getMobile(),
			        profile.getEmail(),
			        channelID);
    }

	public TmnProfile getProfile(Integer channelID, String sessionID, String truemoneyID) {
        GetProfileResponse profile = this.tmnProfileProxyClient.getProfile(createGetProfileRequest(channelID, sessionID, truemoneyID));
        if (profile == null) {
            throw new ProfileNotFoundException();
        }
        TmnProfile tmnProfile = new TmnProfile();
        tmnProfile.setFullname(profile.getFullName() != null ? profile.getFullName() : "");
        tmnProfile.setBalance(profile.getEwalletBalance());
        tmnProfile.setMobileNumber(profile.getMobile());
        tmnProfile.setEmail(profile.getEmail());
        tmnProfile.setHasPassword(profile.getHasPassword());
        tmnProfile.setHasPin(profile.getHasPin());
        String imageFileName = getProfileValue(profile, ProfileKey.profilepic200x200);
        tmnProfile.setImageFileName(imageFileName);
        String imageFileStatus = getProfileValue(profile, ProfileKey.profilepicflag);
        tmnProfile.setProfileImageStatus("1".equals(imageFileStatus));
        return tmnProfile;
    }
	
	public List<Favorite> getListFavorite(Integer channelID, String sessionID, String tmnID, 
			String serviceType) {
        ListFavoriteRequest listFavoriteRequest = createListFavoriteRequest(channelID, sessionID, tmnID, serviceType);
        ListFavoriteResponse listFavoriteResponse = this.tmnProfileProxyClient.listFavorite(listFavoriteRequest);
        FavoriteContext[] favoriteContext = listFavoriteResponse.getFavoriteList();
        List<Favorite> favorites = createFavorites(favoriteContext);
        return favorites;
    }

    public Boolean isFavoritable(Integer channelID, String sessionID,
            String tmnID, String serviceType, String serviceCode,
            String reference1) {
        try {
        	IsFavoritableRequest favRequest = createIsFavoritableRequest(channelID,sessionID,tmnID,serviceType,serviceCode,reference1);
            StandardBizResponse  standardBizResponse =  this.tmnProfileProxyClient.isFavoritable(favRequest);
            return SUCCESS_CODE.equals(standardBizResponse.getResultCode());
        } catch (FailResultCodeException e) {
        	logger.error(e.getMessage());
        	return false;
        }
    }

    public Boolean isFavorited(Integer channelID, String sessionID,
            String tmnID, String serviceType, String serviceCode,
            String reference1) {
        try {
        	IsFavoritedRequest favRequest = createIsFavoritedRequest(channelID,sessionID,tmnID,serviceType,serviceCode,reference1);
        	StandardBizResponse  standardBizResponse =  this.tmnProfileProxyClient.isFavorited(favRequest);
            return SUCCESS_CODE.equals(standardBizResponse.getResultCode());
        } catch (FailResultCodeException e) {
        	logger.error(e.getMessage());
        	return false;
        }
    }

    public Favorite addFavorite(Integer channelID, String sessionID,
                    String tmnID, Favorite favorite) {
        AddFavoriteRequest addFavoriteRequest = createAddFavoriteRequest(channelID, sessionID, tmnID, favorite);
        AddFavoriteResponse addFavoriteResponse = this.tmnProfileProxyClient.addFavorite(addFavoriteRequest);
        Long favoriteID = new Long(addFavoriteResponse.getFavorite().getFavoriteId());
        favorite.setFavoriteID(favoriteID);
        return favorite;
    }

    public Boolean removeFavorite(Integer channelID, String sessionID,
            String tmnID, String serviceCode,
            String reference1) {
	    DeleteFavoriteRequest deleteFavoriteRequest = createRemoveFavoriteRequest(
	            channelID, sessionID, tmnID, serviceCode, reference1);
	    StandardBizResponse standardBizResponse = this.tmnProfileProxyClient.removeFavorite(deleteFavoriteRequest);
	    return verifySuccess(standardBizResponse);
    }

	public void logout(Integer channelID, String sessionID, String truemoneyID) {
    	this.tmnSecurityProxyClient.terminateSession(createNewAccessRequest(channelID, sessionID, truemoneyID));
    }

	public void changePin(Integer channelID, String sessionID, String truemoneyID, String oldPin, String pin, String loginID) {
		ChangePinRequest changePinRequest = createChangePinRequest(channelID, sessionID, truemoneyID, oldPin, pin, loginID);
		this.tmnProfileProxyClient.changePin(changePinRequest);
	}

	public void changePassword(Integer channelID, String sessionID, String truemoneyID, String oldPassword, String password, String loginID) {
		ChangePasswordRequest changePasswordRequest = createChangePasswordRequest(channelID, sessionID, truemoneyID, oldPassword, password, loginID);
		this.tmnProfileProxyClient.changePassword(changePasswordRequest);
	}
	
	public void changeFullname(Integer channelID, String sessionID, String truemoneyID, String fullname) {
		UpdateProfileRequest updateProfileRequest = createUpdateFullnameRequest(channelID, sessionID, truemoneyID, fullname);
		this.tmnProfileProxyClient.updateProfile(updateProfileRequest);
	}
	
	public void changeProfileImage(Integer channelID, String sessionID, String truemoneyID, String profileImage) {
		UpdateProfileRequest updateProfileRequest = createUpdateProfileImageRequest(channelID, sessionID, truemoneyID, profileImage);
		this.tmnProfileProxyClient.updateProfile(updateProfileRequest);
	}
	
	public void changeProfileImageStatus(Integer channelID, String sessionID,
			String truemoneyID, Boolean status) {
		UpdateProfileRequest updateProfileRequest = createUpdateProfileImageStatusRequest(channelID, sessionID, truemoneyID, status);
		this.tmnProfileProxyClient.updateProfile(updateProfileRequest);
	}

	private UpdateProfileRequest createUpdateFullnameRequest(Integer channelID, String sessionID, String truemoneyID, String fullname) {
		UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
		updateProfileRequest.setChannelId(channelID);
		updateProfileRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		updateProfileRequest.setProfileKey(new String[] { ProfileKey.fullname });
		updateProfileRequest.setProfileValue(new String[] { fullname });
		return updateProfileRequest;
	}
	
	private UpdateProfileRequest createUpdateProfileImageRequest(Integer channelID, String sessionID, String truemoneyID, String profileImage) {
		UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
		updateProfileRequest.setChannelId(channelID);
		updateProfileRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		updateProfileRequest.setProfileKey(new String[] { ProfileKey.profilepic200x200 });
		updateProfileRequest.setProfileValue(new String[] { profileImage });
		return updateProfileRequest;
	}
	
	private UpdateProfileRequest createUpdateProfileImageStatusRequest(
			Integer channelID, String sessionID, String truemoneyID,
			Boolean status) {
		UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
		updateProfileRequest.setChannelId(channelID);
		updateProfileRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		updateProfileRequest.setProfileKey(new String[] { ProfileKey.profilepicflag });
		updateProfileRequest.setProfileValue(new String[] { status ? "1" : "0" });
		return updateProfileRequest;
	}
	
    private AddFavoriteRequest createAddFavoriteRequest(Integer channelID,
            String sessionID, String tmnID, Favorite favorite) {
        AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest();
        addFavoriteRequest.setAmount(favorite.getAmount());
        addFavoriteRequest.setChannelId(channelID);
        addFavoriteRequest.setReference1(favorite.getRef1());
        addFavoriteRequest.setServiceCode(favorite.getServiceCode());
        addFavoriteRequest.setServiceType(favorite.getServiceType());
        addFavoriteRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
        return addFavoriteRequest;
    }

    private IsFavoritableRequest createIsFavoritableRequest(Integer channelID, String sessionID,
                    String tmnID, String serviceType, String serviceCode,
                    String reference1){
        IsFavoritableRequest favoritableRequest = new IsFavoritableRequest();
        favoritableRequest.setChannelId(channelID);
        favoritableRequest.setServiceType(serviceType);
        favoritableRequest.setServiceCode(serviceCode);
        favoritableRequest.setReference1(reference1);
        favoritableRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
        return favoritableRequest;
    }

    private IsFavoritedRequest createIsFavoritedRequest(Integer channelID, String sessionID,
                    String tmnID, String serviceType, String serviceCode,
                    String reference1){
        IsFavoritedRequest favoritedRequest = new IsFavoritedRequest();
        favoritedRequest.setChannelId(channelID);
        favoritedRequest.setServiceType(serviceType);
        favoritedRequest.setServiceCode(serviceCode);
        favoritedRequest.setReference1(reference1);
        favoritedRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
        return favoritedRequest;
    }

    private com.tmn.core.api.message.StandardBizRequest createNewAccessRequest(Integer channelID, String sessionID, String truemoneyID) {
    	com.tmn.core.api.message.StandardBizRequest standardBizRequest = new com.tmn.core.api.message.StandardBizRequest();
        standardBizRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
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
    
    private GetProfileRequest createGetProfileRequest(Integer channelID, String sessionID, String truemoneyID) {
    	GetProfileRequest profileRequest = new GetProfileRequest();
    	profileRequest.setChannelId(channelID);
    	profileRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
    	profileRequest.setProfileKey(new String[] {ProfileKey.profilepic200x200});
    	return profileRequest;
	}

    private SecurityContext createSecurityContext(String sessionID, String truemoneyID) {
    	SecurityContext securityContext = new SecurityContext();
    	securityContext.setSessionId(sessionID);
    	securityContext.setTmnId(truemoneyID);
    	return securityContext;
	}

	private DeleteFavoriteRequest createRemoveFavoriteRequest(
            Integer channelID, String sessionID, String tmnID,
            String serviceCode, String reference1) {
        DeleteFavoriteRequest deleteFavoriteRequest = new DeleteFavoriteRequest();
        deleteFavoriteRequest.setChannelId(channelID);
        deleteFavoriteRequest.setServiceCode(serviceCode);
        deleteFavoriteRequest.setReference1(reference1);
        deleteFavoriteRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
        return deleteFavoriteRequest;
    }

    private List<Favorite> createFavorites(FavoriteContext[] favoriteContext) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        List<Favorite> list = new ArrayList<Favorite>();
        try {
            if(favoriteContext!=null) {
                for(FavoriteContext context : favoriteContext){
                    Favorite favorite = new Favorite();
                    favorite.setAmount(context.getAmount());
                    favorite.setFavoriteID(new Long(context.getFavoriteId()));
                    favorite.setRef1(context.getReference1());
                    favorite.setServiceCode(context.getServiceCode());
                    favorite.setServiceType(context.getServiceType());
                    favorite.setDate(df.parse(context.getCreatedDate()));
                    list.add(favorite);
                }
            }
        } catch (ParseException e) {
            throw new ServiceInventoryException(500, "2001", "Invalid favorite date", "TMN-SERVICE-INVENTORY");
        }
        return list;
    }
    
	private ChangePinRequest createChangePinRequest(Integer channelID,
			String sessionID, String truemoneyID, String oldPin, String pin, String loginID) {
		ChangePinRequest changePinRequest = new ChangePinRequest();
		changePinRequest.setChannelId(channelID);
		changePinRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		changePinRequest.setOldPin(encryptSHA1(oldPin, sessionID)); 
		changePinRequest.setNewPin(pin);
		changePinRequest.setLoginId(loginID);
		return changePinRequest;
	}
	
	private ChangePasswordRequest createChangePasswordRequest(Integer channelID,
			String sessionID, String truemoneyID, String oldPassword,
			String password, String loginID) {
		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
		changePasswordRequest.setChannelId(channelID);
		changePasswordRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		changePasswordRequest.setOldPassword(encryptSHA1(oldPassword, sessionID)); 
		changePasswordRequest.setNewPassword(password);
		changePasswordRequest.setLoginId(loginID);
		return changePasswordRequest;
	}
	
	private String encryptSHA1(String oldValue, String sessionID) {
		return HashPasswordUtil.encryptSHA1(sessionID + oldValue.toLowerCase()).toUpperCase();
	}
	
	private String getProfileValue(GetProfileResponse profile, String profileKey) {
		String profileValue = "";
		String[] profileKeys = profile.getProfileKey();
		String[] profileValues = profile.getProfileValue();
		if (profileKeys != null && profileKeys.length > 0) {
            for (int i=0; i<profileKeys.length; i++) {
            	String tempProfileKey = profileKeys[i];
            	if (tempProfileKey != null && tempProfileKey.equals(profileKey)) {
            		profileValue = profileValues[i];
            	}
            }
		}
		return profileValue;
	}

    public static class ProfileNotFoundException extends ServiceInventoryException {
        private static final long serialVersionUID = 7328535407875381185L;
        public ProfileNotFoundException() {
                super(500, "1008", "Profile not found", "TMN-SERVICE-INVENTORY");
        }
    }

    private ListFavoriteRequest createListFavoriteRequest(Integer channelID, String sessionID, 
			String tmnID, String serviceType) {
		ListFavoriteRequest listFavoriteRequest = new ListFavoriteRequest();
        listFavoriteRequest.setChannelId(channelID);
        listFavoriteRequest.setServiceType(serviceType);
        listFavoriteRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
		return listFavoriteRequest;
	}

    private Boolean verifySuccess(StandardBizResponse standardBizResponse) {
		return SUCCESS_CODE.equals(standardBizResponse.getResultCode());
	}

    public void setTmnSecurityProxy(TmnSecurityProxyClient tmnSecurityProxy) {
        this.tmnSecurityProxyClient = tmnSecurityProxy;
    }

	public void setTmnProfileProxy(TmnProfileProxyClient tmnProfileProxyClient) {
		this.tmnProfileProxyClient = tmnProfileProxyClient;
	}

}