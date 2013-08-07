package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.exception.ValidationException;

@Controller
@RequestMapping(value="/ewallet")
public class ForgotPasswordController {

	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
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
	
	@RequestMapping(value = "/profile/password/verify-otp", method = RequestMethod.POST)
	public @ResponseBody String verifyOTP(
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody VerifyResetPassword verifyResetPassword) {

		validateRequestParam(channelID);

		return forgotPasswordService.verifyOTP(channelID, verifyResetPassword);
		
	}
	
	@RequestMapping(value = "/profile/password/confirm-reset", method = RequestMethod.POST)
	public @ResponseBody String confirmResetPassword(		   
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID,
		   @RequestBody ResetPassword resetPassword) {

		validateRequestParam(channelID);

		return forgotPasswordService.confirmResetPassword(channelID, resetPassword);
		
	}
	
	@RequestMapping(value = "/profile/password/resend-otp/{resetPasswordID}", method = RequestMethod.POST)
	public @ResponseBody VerifyResetPassword resendOTP(
		   @PathVariable String resetPasswordID,
		   @RequestParam(value = "channelID", defaultValue="-1") Integer channelID) {

		validateRequestParam(channelID);

		return forgotPasswordService.resendOTP(channelID, resetPasswordID);
	}
	
	private void validateRequestParam(Integer channelID) {
		if (channelID == -1) {
			throw new ValidationException("-1", "Validate error: channelID is null or empty.");
		}
	}
	
}
