package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@Controller
public class TmnProfileController extends BaseController {
	
	@Autowired
	private TmnProfileService tmnProfileService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(
		@RequestBody Login login, 
		@RequestParam Integer channelID,
		@RequestParam String deviceID,
		@RequestParam String deviceType,
		@RequestParam String deviceVersion,
		@RequestParam String clientIP,
		WebRequest request)
			throws SignonServiceException {
		return tmnProfileService.login(login, channelID, deviceID, deviceType, deviceVersion, clientIP);
	}
	
	@RequestMapping(value = "/getprofile/{accesstoken}/{checksum}", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(
		@PathVariable String accesstoken,
		@PathVariable String checksum,
		@RequestParam Integer channelID,
		WebRequest request)
			throws SignonServiceException {
		return tmnProfileService.getTruemoneyProfile(accesstoken, checksum, channelID);
	}
	
	
}
