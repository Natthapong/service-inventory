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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
@RequestMapping(value="/ewallet")
public class TmnProfileController extends BaseController {

	@Autowired
	private TmnProfileService tmnProfileService;
	
	@Autowired 
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(
		@RequestParam(value = "channelID", defaultValue="-1") Integer channelID, 
		@RequestBody Login login)
			throws SignonServiceException {
		validateRequestParam(channelID);
		return tmnProfileService.login(channelID, login);
	}

	@RequestMapping(value = "/profile/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody TmnProfile getTruemoneyProfile(
		@PathVariable String accessTokenID)
			throws ServiceInventoryException {
		TmnProfile tmnProfile = tmnProfileService.getTruemoneyProfile(accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return tmnProfile;
	}

	@RequestMapping(value = "/balance/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody BigDecimal getBalance(
		@PathVariable String accessTokenID)
			throws ServiceInventoryException {
		BigDecimal balance = tmnProfileService.getEwalletBalance(accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return balance;
	}
	
	@RequestMapping(value = "/logout/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody String logout(
		@PathVariable String accessTokenID) {
		return tmnProfileService.logout(accessTokenID);
	}
	
	@RequestMapping(value = "/profiles/validate-email", method = RequestMethod.POST)
	public @ResponseBody String isExistRegistered(
		@RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		@RequestBody String email) 
			throws ServiceInventoryException {
		if (channelID == -1) {
			throw new ValidateException("-1", "Validate error: channelID is null or empty.");
		}
		return tmnProfileService.validateEmail(channelID, email);
	}
	
	@RequestMapping(value = "/profiles", method = RequestMethod.POST)
	public @ResponseBody OTP sendOTPConfirm(
		@RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		@RequestBody TmnProfile tmnProfile) 
			throws ServiceInventoryException {
		validateRequestParam(channelID);
		return tmnProfileService.sendOTPConfirm(channelID, tmnProfile);
	}
	
	@RequestMapping(value = "/profiles/verify-otp", method = RequestMethod.POST)
	public @ResponseBody TmnProfile confirmCreateTruemoneyProfile(
		@RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		@RequestBody OTP otp) 
			throws ServiceInventoryException {
		validateRequestParam(channelID);
		return tmnProfileService.confirmCreateProfile(channelID, otp);
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
	private void validateRequestParam(Integer channelID) {
		if (channelID == -1) {
			throw new ValidateException("-1", "Validate error: channelID is null or empty.");
		}		
	}
	
}
