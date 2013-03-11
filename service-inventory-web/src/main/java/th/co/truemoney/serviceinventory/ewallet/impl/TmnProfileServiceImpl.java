package th.co.truemoney.serviceinventory.ewallet.impl;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SignonResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.profile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.security.TmnSecurityProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

public class TmnProfileServiceImpl implements TmnProfileService {

	@Autowired
	private TmnSecurityProxy tmnSecurityProxy;
	
	@Autowired
	private TmnProfileProxy tmnProfileProxy;
		
	@Override
	public ServiceResponse<TmnProfile> login(Login login) {

		try {
			// Create Request ID
			SignonRequest signonRequest = new SignonRequest();
			signonRequest.setInitiator(login.getUsername());
			signonRequest.setPin(login.getPassword());
			signonRequest.setChannelId("41");
			
			SignonResponse signonResponse = this.tmnSecurityProxy.signon(signonRequest);
			
			ServiceResponse<TmnProfile> serviceResponse = new ServiceResponse<TmnProfile>(
					ServiceInventoryException.NAMESPACE, 
					ServiceInventoryException.Code.SUCCESS, 
					"Success");
			TmnProfile tmnProfile = new TmnProfile(signonResponse.getSessionId(), signonResponse.getTmnId());
			serviceResponse.setBody(tmnProfile);
			return serviceResponse;
		} catch (EwalletException e) {
			throw new SignonServiceException(e.getCode(), "fail to sign on : " + e.getCode(), e.getNamespace());
		}
	}
	
	@Override
	public ServiceResponse<StandardBizResponse> extend(TmnProfile tmnProfile) {
		try {
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			SecurityContext securityContext = new SecurityContext(tmnProfile.getSessionID(), tmnProfile.getTruemoneyID());
			standardBizRequest.setSecurityContext(securityContext);
			StandardBizResponse standardBizResponse = this.tmnSecurityProxy.extendSession(standardBizRequest);
			ServiceResponse<StandardBizResponse> serviceResponse = new ServiceResponse<StandardBizResponse>(
					ServiceInventoryException.NAMESPACE, 
					ServiceInventoryException.Code.SUCCESS, 
					"Success");
			serviceResponse.setBody(standardBizResponse);
			return serviceResponse;
		} catch (ServiceInventoryException e) {
			throw e;
		}
	}

	@Override
	public ServiceResponse<StandardBizResponse> logout(TmnProfile tmnProfile) {
		try {
			StandardBizRequest standardBizRequest = new StandardBizRequest();
			SecurityContext securityContext = new SecurityContext(tmnProfile.getSessionID(), tmnProfile.getTruemoneyID());
			standardBizRequest.setSecurityContext(securityContext);
			StandardBizResponse standardBizResponse = this.tmnSecurityProxy.terminateSession(standardBizRequest);
			ServiceResponse<StandardBizResponse> serviceResponse = new ServiceResponse<StandardBizResponse>(
					ServiceInventoryException.NAMESPACE, 
					ServiceInventoryException.Code.SUCCESS, 
					"Success");
			serviceResponse.setBody(standardBizResponse);
			return serviceResponse;
		} catch (ServiceInventoryException e) {
			throw e;
		}
	}
	
	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}
	
	public void setTmnSecurityProxy(TmnSecurityProxy tmnSecurityProxy) {
		this.tmnSecurityProxy = tmnSecurityProxy;
	}

}
