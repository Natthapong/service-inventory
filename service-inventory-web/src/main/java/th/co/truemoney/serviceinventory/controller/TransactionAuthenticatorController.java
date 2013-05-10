package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/authen")
public class TransactionAuthenticatorController {

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@Autowired
	private TransactionAuthenService transactionAuthenService;

	@RequestMapping(value = "/draft/{draftID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP submitTopUpMobileDraftRequest(
			@PathVariable String draftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
		return transactionAuthenService.requestOTP(draftID, accessTokenID);
	}

	@RequestMapping(value = "/draft/{draftID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody DraftTransaction.Status verifyOTPToppingMobile(
			@PathVariable String draftID,
			@PathVariable String refCode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody OTP otp) {

		if (otp != null) {
			otp.setReferenceCode(refCode);
		}

		extendAccessTokenAsynService.setExpire(accessTokenID);
		return transactionAuthenService.verifyOTP(draftID, otp, accessTokenID);
	}

}
