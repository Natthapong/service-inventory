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
import th.co.truemoney.serviceinventory.exception.ValidateException;
import th.co.truemoney.serviceinventory.util.AccessTokenUtil;

@Controller
@RequestMapping(value="/ewallet")
public class TmnProfileController extends BaseController {

	@Autowired
	private TmnProfileService tmnProfileService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(@RequestParam(value = "channelId", defaultValue="-1") Integer channelId, @RequestBody Login login)
			throws SignonServiceException {
		if (channelId == -1) {
			throw new ValidateException("-1", "Validate error: channelId is null or empty.");
		}
		return tmnProfileService.login(channelId, login);
	}

	@RequestMapping(value = "/getprofile/{accesstoken}/{checksum}", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(
		@PathVariable String accesstoken,
		@PathVariable String checksum)
			throws SignonServiceException {
		if (!AccessTokenUtil.isValidCheckSum(checksum, accesstoken, accesstoken)) {
			throw new ValidateException("-1", "Invalid checksum value");
		}
		return tmnProfileService.getTruemoneyProfile(accesstoken, checksum);
	}


}
