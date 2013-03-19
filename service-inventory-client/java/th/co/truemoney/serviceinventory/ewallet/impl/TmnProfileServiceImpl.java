package th.co.truemoney.serviceinventory.ewallet.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.GetBasicProfileResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = Logger.getLogger(TmnProfileServiceImpl.class);

	@Autowired @Qualifier("accessTokenMemoryRepository")
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

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
			
			AccessToken accessToken = AccessToken.generateNewToken(signonResponse.getSessionId(),
					signonResponse.getTmnId(),
					login.getUsername(),
					profileResponse.getMobile(),
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
	public TmnProfile getTruemoneyProfile(String accessTokenID, String checksum)
			throws ServiceInventoryException {
		try {
			AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
			logger.debug("retrieve access Token: "+accessToken.toString());

			Integer channelId = accessToken.getChannelID();

			SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			standardBizRequest.setChannelId(channelId);
			standardBizRequest.setSecurityContext(securityContext);
			GetBasicProfileResponse profileResponse = this.tmnProfileProxy.getBasicProfile(standardBizRequest);
			TmnProfile tmnProfile = new TmnProfile(profileResponse.getFullName(), profileResponse.getEwalletBalance());
			tmnProfile.setMobileno(profileResponse.getMobile());
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
	public void logout(String accessToken) {
		// TODO Auto-generated method stub

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
