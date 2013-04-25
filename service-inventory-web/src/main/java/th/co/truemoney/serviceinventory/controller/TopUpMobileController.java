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
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Controller
@RequestMapping(value = "/top-up/mobile")
public class TopUpMobileController {

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;
	
	@Autowired
	private TopUpMobileService topUpMobileService;
	
	
	@RequestMapping(value = "/draft", method = RequestMethod.POST)
	public @ResponseBody TopUpMobileDraft verifyAndCreateTopUpMobileDraft(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody TopUpMobileDraft draft) {
		extendExpireAccessToken(accessTokenID);
		TopUpMobile topUpInfo = draft.getTopUpMobileInfo();
		return topUpMobileService.verifyAndCreateTopUpMobileDraft(topUpInfo.getMobileNumber(), topUpInfo.getAmount(), accessTokenID);
	}
	
	@RequestMapping(value = "/draft/{topUpMobileDraftID}", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileDraft getTopUpMobileDraftDetail(
			@PathVariable String topUpMobileDraftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return topUpMobileService.getTopUpMobileDraftDetail(topUpMobileDraftID, accessTokenID);
	}

	@RequestMapping(value = "/draft/{topUpMobileDraftID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP submitTopUpMobileDraftRequest(
			@PathVariable String topUpMobileDraftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return topUpMobileService.sendOTP(topUpMobileDraftID, accessTokenID);
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
		return topUpMobileService.confirmTopUpMobile(topUpMobileDraftID, otp, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileTransaction.Status getToppingMobileStatus(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return topUpMobileService.getTopUpMobileStatus(transactionID, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
	public @ResponseBody TopUpMobileTransaction getTransactionInfo(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return topUpMobileService.getTopUpMobileResult(transactionID, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
