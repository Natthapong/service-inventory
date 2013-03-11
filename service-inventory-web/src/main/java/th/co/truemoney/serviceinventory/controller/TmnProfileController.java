package th.co.truemoney.serviceinventory.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.serviceinventory.common.domain.ServiceRequest;
import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@SuppressWarnings("rawtypes")
@Controller
public class TmnProfileController extends BaseController {
	
	private static Logger logger = Logger.getLogger(TmnProfileController.class);

	@Autowired
	private TmnProfileService tmnProfileService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody ServiceResponse<TmnProfile> login(
		@RequestBody ServiceRequest<Login> serviceRequest,	WebRequest request)
			throws ServiceInventoryException {
		Login login = serviceRequest.getBody();
		logger.debug("request trans id = "+serviceRequest.getRequestTransactionID());
		logger.debug("username = "+login.getUsername());
		return tmnProfileService.login(login);
	}
	
	@RequestMapping(value = "/extend", method = RequestMethod.POST)
	public @ResponseBody ServiceResponse extend(
		@RequestBody ServiceRequest<TmnProfile> serviceRequest, WebRequest request)
			throws ServiceInventoryException {
		TmnProfile tmnProfile = serviceRequest.getBody();
		return tmnProfileService.extend(tmnProfile);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public @ResponseBody ServiceResponse logout(
		@RequestBody ServiceRequest<TmnProfile> serviceRequest, WebRequest request)
			throws ServiceInventoryException {
		TmnProfile tmnProfile = serviceRequest.getBody();
		return tmnProfileService.logout(tmnProfile);
	}
	
}
