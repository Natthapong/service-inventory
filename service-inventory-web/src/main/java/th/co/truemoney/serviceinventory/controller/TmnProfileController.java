package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.bean.LoginRequest;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ChangePin;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ValidationException;

@Controller
@RequestMapping(value="/ewallet")
public class TmnProfileController {

	@Autowired
	private TmnProfileService tmnProfileService;
	
	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(@Valid @RequestBody LoginRequest loginRequest) {

		return tmnProfileService.login(
				loginRequest.getUserLogin(),
				loginRequest.getAppLogin());
	}

	@RequestMapping(value = "/profile/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(@PathVariable String accessTokenID) {

		extendExpireAccessToken(accessTokenID);

		TmnProfile tmnProfile = tmnProfileService.getTruemoneyProfile(accessTokenID);

		return tmnProfile;
	}

	@RequestMapping(value = "/profile/balance/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody BigDecimal getBalance(@PathVariable String accessTokenID) {

		extendExpireAccessToken(accessTokenID);

		BigDecimal balance = tmnProfileService.getEwalletBalance(accessTokenID);

		return balance;
	}

	@RequestMapping(value = "/logout/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody String logout(
		   @PathVariable String accessTokenID) {

		return tmnProfileService.logout(accessTokenID);
	}

	@RequestMapping(value = "/profile/validate-email", method = RequestMethod.POST)
	public @ResponseBody String isExistRegistered(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody String email) {

		validateRequestParam(channelID);

		return tmnProfileService.validateEmail(channelID, email);
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public @ResponseBody OTP createTruemoneyProfile(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody TmnProfile tmnProfile) {

		validateRequestParam(channelID);

		return tmnProfileService.createProfile(channelID, tmnProfile);
	}

	@RequestMapping(value = "/profile/verify-otp", method = RequestMethod.POST)
	public @ResponseBody TmnProfile confirmCreateTruemoneyProfile(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody OTP otp) {

		validateRequestParam(channelID);

		return tmnProfileService.confirmCreateProfile(channelID, otp);
	}

	@RequestMapping(value = "/profile/change-pin", method = RequestMethod.PUT)
	public @ResponseBody String changePin(
		   @RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID,
		   @RequestBody ChangePin changePin) {

		extendExpireAccessToken(accessTokenID);
		
		return tmnProfileService.changePin(accessTokenID, changePin);
	}
	
	@RequestMapping(value = "/profile/{accessTokenID}", method = RequestMethod.PUT)
	public @ResponseBody TmnProfile updateTruemoneyProfile(
		   @PathVariable String accessTokenID,
		   @RequestBody TmnProfile tmnProfile) {

		extendExpireAccessToken(accessTokenID);
		
		return tmnProfileService.updateTruemoneyProfile(accessTokenID, tmnProfile);
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

	private void validateRequestParam(Integer channelID) {
		if (channelID == -1) {
			throw new ValidationException("-1", "Validate error: channelID is null or empty.");
		}
	}

}
