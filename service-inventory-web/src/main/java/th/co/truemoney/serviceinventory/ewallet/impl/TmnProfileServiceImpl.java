package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = LoggerFactory.getLogger(TmnProfileServiceImpl.class);

	@Autowired @Qualifier("accessTokenRedisRepository")
	private AccessTokenRepository accessTokenRepo;
	
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private OTPService otpService;

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Autowired
	private EwalletSoapProxy ewalletSoapProxy;
	
	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	
	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;
	
	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;


	@Override
	public String login(Integer channelID, Login login)
				throws SignonServiceException {
		try {
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

		} catch (EwalletException e) {
			throw new SignonServiceException(e.getCode(),
				"tmnSecurityProxy.signon response: " + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new SignonServiceException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accessTokenID)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
			logger.debug("retrieve access Token: "+accessToken.toString());

			SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			standardBizRequest.setChannelId(accessToken.getChannelID());
			standardBizRequest.setSecurityContext(securityContext);
			GetBasicProfileResponse profileResponse = this.tmnProfileProxy.getBasicProfile(standardBizRequest);
			TmnProfile tmnProfile = new TmnProfile(profileResponse.getFullName(), profileResponse.getEwalletBalance());
			tmnProfile.setMobileno(profileResponse.getMobile());
			tmnProfile.setEmail(profileResponse.getEmail());
			tmnProfile.setType(profileResponse.getProfileType());
			tmnProfile.setStatus(profileResponse.getStatusId());
			return tmnProfile;
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
				"tmnProfileProxy.getBasicProfile response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

	@Override
	public BigDecimal getEwalletBalance(String accessTokenID)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
			logger.debug("retrieve access Token: "+accessToken.toString());

			SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			standardBizRequest.setChannelId(accessToken.getChannelID());
			standardBizRequest.setSecurityContext(securityContext);
			GetBalanceResponse balanceResponse = this.ewalletSoapProxy.getBalance(standardBizRequest);
			return balanceResponse.getCurrentBalance();
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
				"ewalletSoapProxy.getBalance response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

	@Override
	public String logout(String accessTokenID) {
		try {
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

		} catch (EwalletException e) {
			logger.error(e.getMessage(), e);
		} catch (ServiceUnavailableException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}
	
	@Override
	public String validateEmail(Integer channelID, String email) throws ServiceInventoryException {
		AdminSecurityContext adminSecurityContext = new AdminSecurityContext();
		adminSecurityContext.setInitiator("si.tmnprofile");
		adminSecurityContext.setPin("0000");
		IsCreatableRequest isCreatableRequest = new IsCreatableRequest();
		isCreatableRequest.setAdminSecurityContext(adminSecurityContext);
		isCreatableRequest.setChannelId(channelID);
		isCreatableRequest.setLoginId(email);		
		
		tmnProfileAdminProxy.isCreatable(isCreatableRequest);
		
		return email;
	}

	@Override
	public String createProfile(Integer channelID, TmnProfile tmnProfile) {
		try {
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(channelID, tmnProfile.getMobileno());
			
			tmnProfileAdminProxy.isCreatable(isCreatableRequest);
			
			return otpService.send(tmnProfile.getMobileno());
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(),
				"tmnProfileAdminProxy.isCreatable response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				e.getMessage(), e.getNamespace());
		}
	}

	private IsCreatableRequest createIsCreatableRequest(Integer channelID, String loginID) {
		IsCreatableRequest isCreatableRequest = new IsCreatableRequest();
		isCreatableRequest.setChannelId(channelID);		
		isCreatableRequest.setLoginId(loginID);
		AdminSecurityContext adminSecurityContext = new AdminSecurityContext(tmnProfileInitiator, tmnProfilePin);
		isCreatableRequest.setAdminSecurityContext(adminSecurityContext);
		return isCreatableRequest;
	}

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, String mobileno, OTP otp) {
		
		String localOTP = otpService.getOTPString(mobileno);
		if(!localOTP.equals(otp.getOtpString())) {
			throw new ServiceInventoryException(SignonServiceException.Code.OTP_NOT_MATCH, "otp string not match");
		}
		
		TmnProfile tmnProfile = profileRepository.getTmnProfile(mobileno);
		
		CreateTmnProfileRequest tmnProfileRequest = new CreateTmnProfileRequest();		
		tmnProfileRequest.setChannelId(channelID);
		tmnProfileRequest.setEmail(tmnProfile.getEmail());
		tmnProfileRequest.setFullName(tmnProfile.getFullname());
		tmnProfileRequest.setMobile(tmnProfile.getMobileno());
		tmnProfileRequest.setPassword(tmnProfile.getPassword());
		tmnProfileRequest.setThaiId(tmnProfile.getThaiID());
				
		tmnProfileProxy.createTmnProfile(tmnProfileRequest);
		
		return tmnProfile;
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}
	
	public void setAccessTokenRepository(AccessTokenMemoryRepository accessTokenMemoryRepository) {
		this.accessTokenRepo = accessTokenMemoryRepository;
	}

	public void setTmnProfileAdminProxy(TmnProfileAdminProxy tmnProfileAdminProxy) {
		this.tmnProfileAdminProxy = tmnProfileAdminProxy;
	}
	
	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;		
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	private SignonRequest createSignOnRequest(Integer channelID, Login login) {
		SignonRequest signonRequest = new SignonRequest();
		signonRequest.setInitiator(login.getUsername());
		signonRequest.setPin(login.getHashPassword());
		signonRequest.setChannelId(channelID);

		return signonRequest;
	}
}
