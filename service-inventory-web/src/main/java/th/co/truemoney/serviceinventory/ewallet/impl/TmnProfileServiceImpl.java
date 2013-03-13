package th.co.truemoney.serviceinventory.ewallet.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
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
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.util.AccessTokenUtil;

public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = Logger.getLogger(TmnProfileServiceImpl.class);
	
	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;
	
	@Autowired
	private TmnProfileProxy tmnProfileProxy;
		
	@Override
	public String login(Login login, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP) 
				throws SignonServiceException {
		try {
			// Create Request ID
			SignonRequest signonRequest = new SignonRequest();
			signonRequest.setInitiator(login.getUsername());
			signonRequest.setPin(login.getHashPassword());
			signonRequest.setChannelId(channelID);
			
			SignonResponse signonResponse = this.tmnSecurityProxy.signon(signonRequest);
			
			String accessToken = this.createAccessToken(login.getUsername(), channelID, 
					deviceID, deviceType, deviceVersion, clientIP, signonResponse.getSessionId());
			
			// add session id and mapping access token into redis
			logger.debug("session ID: "+signonResponse.getSessionId());
			logger.debug("tmn ID: "+ signonResponse.getTmnId());		
			logger.debug("username: "+login.getUsername());

			return accessToken;
			
		} catch (EwalletException e) {
			throw new SignonServiceException(e.getCode(), 
				"tmnSecurityProxy.signon response: " + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new SignonServiceException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), 
				e.getMessage(), e.getNamespace());
		}
	}
		
	@Override
	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum, Integer channelID) 
			throws ServiceInventoryException {
		try {
			String sessionID = "";
			String truemoneyID = "";
			SecurityContext securityContext = new SecurityContext(sessionID, truemoneyID);
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			standardBizRequest.setChannelId(channelID);
			standardBizRequest.setSecurityContext(securityContext);
			GetBasicProfileResponse profileResponse =
				this.tmnProfileProxy.getBasicProfile(standardBizRequest);
			TmnProfile tmnProfile = new TmnProfile(profileResponse.getFullName(), profileResponse.getEwalletBalance());
			tmnProfile.setMobileno(profileResponse.getMobile());
			tmnProfile.setType(profileResponse.getProfileType());	
			tmnProfile.setStatus(profileResponse.getStatusId());
			return new TmnProfile();
		} catch (EwalletException e) {
			throw new SignonServiceException(e.getCode(), 
				"tmnProfileProxy.getBasicProfile response" + e.getCode(), e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new SignonServiceException(Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE), 
				e.getMessage(), e.getNamespace());
		}
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}
	
	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}
	
	public String createAccessToken(String username, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP, String utibaSessionID) {
		return AccessTokenUtil.generateToken(username, channelID, deviceID, deviceType, deviceVersion, clientIP, utibaSessionID);
	}

}
