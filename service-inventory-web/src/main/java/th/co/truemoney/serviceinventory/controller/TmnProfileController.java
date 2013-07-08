package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;
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
import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
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

	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
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

	@RequestMapping(value = "/balance/{accessTokenID}", method = RequestMethod.GET)
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
		extendExpireAccessToken(accessTokenID);
		try {
			List<Activity> activities = activityService.getActivities(accessTokenID);
			return activities;
		} catch (ServiceInventoryException e) {
			throw new ServiceInventoryWebException(Code.GET_ACTIVITY_FAILED, e.getErrorDescription());
		}
	}
	
	@RequestMapping(value = "/activities/{accessTokenID}/detail/{reportID}", method = RequestMethod.GET)
	public @ResponseBody ActivityDetail getActivityDetail(@PathVariable String accessTokenID, @PathVariable Long reportID) {
		extendExpireAccessToken(accessTokenID);
		try {	
			ActivityDetail activityDetail = activityService.getActivityDetail(reportID, accessTokenID);
			return activityDetail;
		} catch (ServiceInventoryException e) {
			throw new ServiceInventoryWebException(Code.GET_ACTIVITY_DETAIL_FAILED, e.getErrorDescription());
		}
	}
	
	@RequestMapping(value = "/favorites/{accessTokenID}" , method = RequestMethod.POST)
	public @ResponseBody Favorite addFavorite(
			@RequestBody Favorite favorite,
			@PathVariable String accessTokenID) {
		
		extendExpireAccessToken(accessTokenID);

		Favorite favoriteResponse = favoriteService.addFavorite(favorite, accessTokenID);
		
		return favoriteResponse;
	}
	
	@RequestMapping(value = "/favorites" , method = RequestMethod.DELETE)
	public @ResponseBody Boolean removeFavorite(
			@RequestParam(value = "serviceCode", defaultValue="") String serviceCode,
			@RequestParam(value = "ref1", defaultValue="") String ref1,
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {
		
		extendExpireAccessToken(accessTokenID);

		return favoriteService.deleteFavorite(serviceCode, ref1, accessTokenID);
	}
	
	@RequestMapping(value = "/favorites" , method = RequestMethod.GET)
	public @ResponseBody List<Favorite> getFavorites(
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		
		List<Favorite> favorites = favoriteService.getFavorites(accessTokenID);
		
		return favorites;
	}
	
	@RequestMapping(value = "/profile/createforgotpassword", method = RequestMethod.POST)
	public @ResponseBody ForgotPassword createForgotPassword(
			@RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
			@RequestBody ForgotPassword request) {
		
		validateRequestParam(channelID);
		
		return forgotPasswordService.createForgotPassword(channelID, request);
	}
	
	@RequestMapping(value = "/profile/password/verify-reset", method = RequestMethod.POST)
	public @ResponseBody VerifyResetPassword verifyResetPassword(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody ResetPassword resetPasswordRequest) {

		validateRequestParam(channelID);

		return forgotPasswordService.verifyResetPassword(channelID, resetPasswordRequest);
	}
	
	@RequestMapping(value = "/profile/password/confirm-reset", method = RequestMethod.POST)
	public @ResponseBody String confirmResetPassword(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody VerifyResetPassword verifyResetPassword) {

		validateRequestParam(channelID);

		return forgotPasswordService.confirmResetPassword(channelID, verifyResetPassword);
		
	}
	
	@RequestMapping(value = "profile/password/resend-otp/{resetPasswordID}", method = RequestMethod.POST)
	public @ResponseBody VerifyResetPassword resendOTP(
		   @PathVariable String resetPasswordID,
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID) {

		validateRequestParam(channelID);

		return forgotPasswordService.resendOTP(channelID, resetPasswordID);
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
