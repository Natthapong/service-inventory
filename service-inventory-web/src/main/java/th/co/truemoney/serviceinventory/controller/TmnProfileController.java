package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@Controller
@RequestMapping(value="/ewallet")
public class TmnProfileController extends BaseController {
	
	@Autowired
	private TmnProfileService tmnProfileService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(@RequestParam Integer channelID, @RequestBody Login login)
			throws SignonServiceException {
		
		return tmnProfileService.login(channelID, login);
	}
	
	@RequestMapping(value = "/getprofile", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(
		@RequestParam Integer channelID,
		@PathVariable String accesstoken,
		@PathVariable String checksum)
			throws SignonServiceException {
		return tmnProfileService.getTruemoneyProfile(channelID, accesstoken, checksum);
	}
	
	
}
