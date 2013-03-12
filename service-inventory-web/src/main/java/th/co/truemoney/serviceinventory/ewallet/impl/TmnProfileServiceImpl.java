package th.co.truemoney.serviceinventory.ewallet.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.profile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.TmnSecurityProxy;
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
			throw new SignonServiceException(e.getCode(), "fail to sign on : " + e.getCode(), e.getNamespace());
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
