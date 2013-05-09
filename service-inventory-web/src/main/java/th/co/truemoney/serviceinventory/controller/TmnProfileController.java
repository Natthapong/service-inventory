package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
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
	private ActivityService activityService;
	
	@Autowired
	private FavoriteService favoriteService;

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

		TmnProfile tmnProfile = tmnProfileService.getTruemoneyProfile(accessTokenID);

		extendExpireAccessToken(accessTokenID);

		return tmnProfile;
	}

	@RequestMapping(value = "/balance/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody BigDecimal getBalance(@PathVariable String accessTokenID) {

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
		   @RequestBody String email) {

		validateRequestParam(channelID);

		return tmnProfileService.validateEmail(channelID, email);
	}

	@RequestMapping(value = "/profiles", method = RequestMethod.POST)
	public @ResponseBody OTP createTruemoneyProfile(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody TmnProfile tmnProfile) {

		validateRequestParam(channelID);

		return tmnProfileService.createProfile(channelID, tmnProfile);
	}

	@RequestMapping(value = "/profiles/verify-otp", method = RequestMethod.POST)
	public @ResponseBody TmnProfile confirmCreateTruemoneyProfile(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody OTP otp) {

		validateRequestParam(channelID);

		return tmnProfileService.confirmCreateProfile(channelID, otp);
	}

	@RequestMapping(value = "/activities/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody List<Activity> getActivities(@PathVariable String accessTokenID) {

		List<Activity> activities = activityService.getActivities(accessTokenID);

		extendExpireAccessToken(accessTokenID);
		
		return activities;
	}
	
	@RequestMapping(value = "/activities/{accessTokenID}/detail/{reportID}", method = RequestMethod.GET)
	public @ResponseBody ActivityDetail getActivityDetail(@PathVariable String accessTokenID, @PathVariable Long reportID) {

		ActivityDetail activityDetail = activityService.getActivityDetail(reportID, accessTokenID);

		extendExpireAccessToken(accessTokenID);
		
		return activityDetail;
	}
	
	@RequestMapping(value = "/favorites/{accessTokenID}" , method = RequestMethod.POST)
	public @ResponseBody Favorite addFavorite(
			@RequestBody Favorite favorite,
			@PathVariable String accessTokenID) {
		System.out.println("accessTokenID : "+ accessTokenID);
		
		Favorite favoriteResponse = favoriteService.addFavorite(favorite, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		
		return favoriteResponse;
	}
	
	@RequestMapping(value = "/favorites" , method = RequestMethod.GET)
	public @ResponseBody List<Favorite> getFavorites(
			@RequestParam(value = "serviceType", defaultValue="") String serviceType,
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {
		System.out.println("serviceType : "+ serviceType);
		System.out.println("accessTokenID : "+ accessTokenID);
		
		List<Favorite> favorites = favoriteService.getFavorites(serviceType, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		
		return favorites;
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
