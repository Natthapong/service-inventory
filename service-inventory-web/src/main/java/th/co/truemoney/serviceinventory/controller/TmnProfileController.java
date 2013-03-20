package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
@RequestMapping(value="/ewallet")
public class TmnProfileController extends BaseController {

	@Autowired
	private TmnProfileService tmnProfileService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(@RequestParam(value = "channelID", defaultValue="-1") Integer channelID, @RequestBody Login login)
			throws SignonServiceException {
		if (channelID == -1) {
			throw new ValidateException("-1", "Validate error: channelID is null or empty.");
		}
		return tmnProfileService.login(channelID, login);
	}

	@RequestMapping(value = "/getprofile/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(
		@PathVariable String accessTokenID)
			throws SignonServiceException {
		return tmnProfileService.getTruemoneyProfile(accessTokenID);
	}

	@RequestMapping(value = "/getbalance/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody BigDecimal getBalance(
		@PathVariable String accessTokenID)
			throws SignonServiceException {
		return tmnProfileService.getEwalletBalance(accessTokenID);
	}
	
	@RequestMapping(value = "/logout/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody String logout(
			@PathVariable String accessTokenID)
	{
		return tmnProfileService.logout(accessTokenID);
	}
	
}
