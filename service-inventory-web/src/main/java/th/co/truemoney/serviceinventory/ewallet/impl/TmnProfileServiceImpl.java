package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBalanceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = LoggerFactory.getLogger(TmnProfileServiceImpl.class);

	@Autowired @Qualifier("accessTokenMemoryRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Autowired
	private EwalletSoapProxy ewalletSoapProxy;

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
			throw new SignonServiceException(e.getCode(),
				"tmnProfileProxy.getBasicProfile response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new SignonServiceException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
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
			throw new SignonServiceException(e.getCode(),
				"ewalletSoapProxy.getBalance response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new SignonServiceException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
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
	public String validateEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createProfile(TmnProfile tmnProfile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TmnProfile confirmCreateProfile(String mobileno, OTP otp) {
		// TODO Auto-generated method stub
		return null;
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

	private SignonRequest createSignOnRequest(Integer channelID, Login login) {
		SignonRequest signonRequest = new SignonRequest();
		signonRequest.setInitiator(login.getUsername());
		signonRequest.setPin(login.getHashPassword());
		signonRequest.setChannelId(channelID);

		return signonRequest;
	}

}
