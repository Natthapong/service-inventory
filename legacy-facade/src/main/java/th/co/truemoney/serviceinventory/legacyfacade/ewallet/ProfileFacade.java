package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ProfileFacade {

	private static final int VALID_CUSTOMER_STATUS = 3;

	private static final String CUSTOMER_TYPE = "C";

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;

	public AccessToken login(Integer channelID, Login login) {

		String username = login.getUsername();
		String password = login.getHashPassword();

		SignonResponse signon = this.tmnSecurityProxy.signon(createGetSignOnRequest(channelID, username, password));

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
				login.getUsername(),
				profile.getMobile(),
				profile.getEmail(),
				channelID);
	}

	public TmnProfile getTmnProfile(AccessToken accessToken) {

		Integer channelID = accessToken.getChannelID();
		String sessionID = accessToken.getSessionID();
		String truemoneyID = accessToken.getTruemoneyID();

		GetBasicProfileResponse profile = this.tmnProfileProxy.getBasicProfile(createAccessRequest(channelID, sessionID, truemoneyID));

		TmnProfile tmnProfile = new TmnProfile(profile.getFullName(), profile.getEwalletBalance());

		tmnProfile.setMobileNumber(profile.getMobile());
		tmnProfile.setEmail(profile.getEmail());
		tmnProfile.setType(profile.getProfileType());
		tmnProfile.setStatus(profile.getStatusId());

		return tmnProfile;
	}

	public void logout(AccessToken accessToken) {

		Integer channelID = accessToken.getChannelID();
		String sessionID = accessToken.getSessionID();
		String truemoneyID = accessToken.getTruemoneyID();

		this.tmnSecurityProxy.terminateSession(createAccessRequest(channelID, sessionID, truemoneyID));
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

	public void verifyValidRegisteringEmail(Integer channelID, String registeringEmail) throws ServiceInventoryException {
		try {
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(tmnProfileInitiator, tmnProfilePin, channelID, registeringEmail);
			tmnProfileAdminProxy.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("email", registeringEmail);
			throw e;
		}
    }

	public void verifyValidRegisteringMobileNumber(Integer channelID, String registeringMobileNumber) throws ServiceInventoryException {
		try {
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(tmnProfileInitiator, tmnProfilePin, channelID, registeringMobileNumber);
			tmnProfileAdminProxy.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("mobileNumber", registeringMobileNumber);
			throw e;
		}
    }

    public void register(Integer channelID, TmnProfile profile) {

    	verifyValidRegisteringEmail(channelID, profile.getEmail());
    	verifyValidRegisteringMobileNumber(channelID, profile.getMobileNumber());

    	CreateTmnProfileRequest createProfileRequest = createTmnProfileRequest(channelID, profile);
    	tmnProfileProxy.createTmnProfile(createProfileRequest);
    }

    private IsCreatableRequest createIsCreatableRequest(String tmnProfileInitiator, String tmnProfilePin, Integer channelID, String loginID) {
        IsCreatableRequest isCreatableRequest = new IsCreatableRequest();
        isCreatableRequest.setChannelId(channelID);
        isCreatableRequest.setLoginId(loginID);

		String encryptedPin = HashPasswordUtil.encryptSHA1(tmnProfileInitiator.toLowerCase() + tmnProfilePin).toLowerCase();

        AdminSecurityContext adminSecurityContext = new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
        isCreatableRequest.setAdminSecurityContext(adminSecurityContext);
        return isCreatableRequest;
    }

    private CreateTmnProfileRequest createTmnProfileRequest(Integer channelID, TmnProfile tmnProfile) {
		CreateTmnProfileRequest tmnProfileRequest = new CreateTmnProfileRequest();
		tmnProfileRequest.setChannelId(channelID);
		tmnProfileRequest.setEmail(tmnProfile.getEmail());
		tmnProfileRequest.setFullName(tmnProfile.getFullname());
		tmnProfileRequest.setMobile(tmnProfile.getMobileNumber());
		tmnProfileRequest.setPassword(tmnProfile.getPassword());
		tmnProfileRequest.setThaiId(tmnProfile.getThaiID());

		return tmnProfileRequest;
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

	public void setTmnProfileAdminProxy(
			TmnProfileAdminProxy tmnProfileAdminProxy) {
		this.tmnProfileAdminProxy = tmnProfileAdminProxy;
	}

	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}

	public void setTmnProfileInitiator(String tmnProfileInitiator) {
		this.tmnProfileInitiator = tmnProfileInitiator;
	}

	public void setTmnProfilePin(String tmnProfilePin) {
		this.tmnProfilePin = tmnProfilePin;
	}
}
