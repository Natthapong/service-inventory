package th.co.truemoney.serviceinventory.ewallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ForgotPasswordServiceImpl implements ForgotPasswordService {
	
	private static Logger logger = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;
	
	@Override
	public ForgotPassword requestForgotPassword(ForgotPassword request)
			throws ServiceInventoryException {
		logger.debug("request forgot password for " + request.getUsername());
		CreateForgotPasswordRequest createForgotPasswordRequest = new CreateForgotPasswordRequest();
		createForgotPasswordRequest.setChannelId(request.getChannelID());
		createForgotPasswordRequest.setLoginId(request.getUsername());
		createForgotPasswordRequest.setThaiId(request.getIdcard());
		
		tmnProfileAdminProxy.createForgotPassword(createForgotPasswordRequest);
		
		return request; //just echo back something
	}

}
