package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Controller
@RequestMapping(value = "/top-up/mobile")
public class TopUpEwalletController {

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/draft", method = RequestMethod.POST)
	public @ResponseBody TopUpMobileDraft verifyAndCreateTopUpMobileDraft(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody TopUpMobileDraft draft) {
		extendExpireAccessToken(accessTokenID);
		return null;
	}
	
	@RequestMapping(value = "/draft/{topUpMobileDraftID}", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileDraft getTopUpMobileDraftDetail(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody TopUpMobileDraft draft) {
		extendExpireAccessToken(accessTokenID);
		return null;
	}

	@RequestMapping(value = "/draft/{topUpMobileDraftID}/otp", method = RequestMethod.POST)
	public @ResponseBody TopUpMobileDraft submitTopUpMobileDraftRequest(
			@PathVariable String topUpMobileDraftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return null;
	}
	
	@RequestMapping(value = "/draft/{topUpMobileDraftID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody TopUpMobileDraft.Status verifyOTPAndPerformToppingMobile(
			@PathVariable String topUpMobileDraftID,
			@PathVariable String refCode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody OTP otp) {
		if (otp != null) {
			otp.setReferenceCode(refCode);
		}
		extendExpireAccessToken(accessTokenID);
		return null;
	}

	@RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileTransaction.Status getToppingMobileStatus(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return null;
	}

	@RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileTransaction getTransactionInfo(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return null;
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
