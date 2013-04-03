package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
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
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.EwalletFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = LoggerFactory.getLogger(TmnProfileServiceImpl.class);

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private OTPService otpService;

//	@Autowired
//	private TmnSecurityProxy tmnSecurityProxy;
//
//	@Autowired
//	private TmnProfileProxy tmnProfileProxy;
//
//	@Autowired
//	private TmnProfileAdminProxy tmnProfileAdminProxy;

	@Autowired
	private EwalletFacade ewalletFacade;

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;

	@Autowired
	private EmailService emailService;


	@Override
	public String login(Integer channelID, Login login)
				throws SignonServiceException {

		// Create Request ID
		SignonRequest signonRequest = createSignOnRequest(channelID, login);
		SignonResponse signonResponse = this.tmnSecurityProxy.signon(signonRequest);

		SecurityContext securityContext = new SecurityContext(signonResponse.getSessionId(), signonResponse.getTmnId());
		StandardBizRequest standardBizRequest = new StandardBizRequest();
		standardBizRequest.setChannelId(channelID);
		standardBizRequest.setSecurityContext(securityContext);
		GetBasicProfileResponse profileResponse = this.tmnProfileProxy.getBasicProfile(standardBizRequest);

		if (profileResponse != null && !profileResponse.getProfileType().equals("C")) {
			throw new SignonServiceException(SignonServiceException.Code.INVALID_PROFILE_TYPE, "Invalid profile type, is not a customer.");
		} else if (profileResponse != null && profileResponse.getStatusId() != 3) {
			throw new SignonServiceException(SignonServiceException.Code.INVALID_PROFILE_STATUS, "Invalid profile status. ("+profileResponse.getStatusId()+")");
		}

		AccessToken accessToken = AccessToken.generateNewToken(signonResponse.getSessionId(),
				signonResponse.getTmnId(),
				login.getUsername(),
				profileResponse.getMobile(),
				profileResponse.getEmail(),
				channelID);

		// add session id and mapping access token into redis
		logger.info("Access token created: " + accessToken);

		accessTokenRepo.save(accessToken);

		return accessToken.getAccessTokenID();
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		logger.debug("retrieve access Token: "+accessToken.toString());

		SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());
		StandardBizRequest standardBizRequest = new StandardBizRequest();
		standardBizRequest.setChannelId(accessToken.getChannelID());
		standardBizRequest.setSecurityContext(securityContext);
		GetBasicProfileResponse profileResponse = this.tmnProfileProxy.getBasicProfile(standardBizRequest);
		TmnProfile tmnProfile = new TmnProfile(profileResponse.getFullName(), profileResponse.getEwalletBalance());
		tmnProfile.setMobileNumber(profileResponse.getMobile());
		tmnProfile.setEmail(profileResponse.getEmail());
		tmnProfile.setType(profileResponse.getProfileType());
		tmnProfile.setStatus(profileResponse.getStatusId());

		return tmnProfile;
	}

	@Override
	public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		return ewalletFacade.getCurrentBalance(accessToken);
	}

	@Override
	public String logout(String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		if (accessToken == null) return "";

		accessTokenRepo.remove(accessTokenID);

		//--- Terminate Session Utiba ---//
		SecurityContext securityContext = new SecurityContext();
		securityContext.setSessionId(accessToken.getSessionID());
		securityContext.setTmnId(accessToken.getTruemoneyID());

		StandardBizRequest standardBizRequest = new StandardBizRequest();
		standardBizRequest.setSecurityContext(securityContext);
		standardBizRequest.setChannelId(accessToken.getChannelID());

		this.tmnSecurityProxy.terminateSession(standardBizRequest);

		return "";
	}

	@Override
    public String validateEmail(Integer channelID, String email) throws ServiceInventoryException {
		try {
			performIsCreatable(channelID, email);
			return email;
		} catch (ServiceInventoryWebException e) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("email", email);
			e.setData(data);
			throw e;
		}
    }

	@Override
    public OTP createProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {
       	performIsCreatable(channelID, tmnProfile.getMobileNumber());
       	OTP otp = otpService.send(tmnProfile.getMobileNumber());
       	profileRepository.saveProfile(tmnProfile);
       	return otp;
    }

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) throws ServiceInventoryException {

		otpService.isValidOTP(otp);

		TmnProfile tmnProfile = profileRepository.getTmnProfile(otp.getMobileNumber());

		performCreateProfile(channelID, tmnProfile);

		performSendWelcomeEmail(tmnProfile.getEmail());

		return tmnProfile;
	}

	private void performSendWelcomeEmail(String email) {
		emailService.sendWelcomeEmail(email, null);
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}

	public void setTmnProfileAdminProxy(TmnProfileAdminProxy tmnProfileAdminProxy) {
		this.tmnProfileAdminProxy = tmnProfileAdminProxy;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public void setTmnProfileInitiator(String tmnProfileInitiator) {
		this.tmnProfileInitiator = tmnProfileInitiator;
	}

	public void setTmnProfilePin(String tmnProfilePin) {
		this.tmnProfilePin = tmnProfilePin;
	}

    private void performIsCreatable(Integer channelID, String loginID) throws ServiceInventoryException {
		IsCreatableRequest isCreatableRequest = createIsCreatableRequest(channelID, loginID);
		tmnProfileAdminProxy.isCreatable(isCreatableRequest);
    }

    private void performCreateProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {
		CreateTmnProfileRequest createTmnProfileRequest = createTmnProfileRequest(channelID, tmnProfile);
		tmnProfileProxy.createTmnProfile(createTmnProfileRequest);
    }

    private IsCreatableRequest createIsCreatableRequest(Integer channelID, String loginID) {
        IsCreatableRequest isCreatableRequest = new IsCreatableRequest();
        isCreatableRequest.setChannelId(channelID);
        isCreatableRequest.setLoginId(loginID);

		String encryptedPin = HashPasswordUtil.encryptSHA1(tmnProfileInitiator.toLowerCase()+tmnProfilePin).toLowerCase();

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

	private SignonRequest createSignOnRequest(Integer channelID, Login login) {
		SignonRequest signonRequest = new SignonRequest();
		signonRequest.setInitiator(login.getUsername());
		signonRequest.setPin(login.getHashPassword());
		signonRequest.setChannelId(channelID);

		return signonRequest;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}



}
