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
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@Controller
@RequestMapping(value="/transfer")
public class P2PTransferController {

	@Autowired
	private P2PTransferService p2pTransferService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/draft", method = RequestMethod.POST)
	public @ResponseBody P2PTransferDraft createTransferDraft(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody P2PTransferDraft draft) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.createAndVerifyTransferDraft(draft.getMobileNumber(), draft.getAmount(), accessTokenID);
	}

	@RequestMapping(value = "/draft/{transferDraftID}", method = RequestMethod.GET)
	public @ResponseBody P2PTransferDraft getTransferDraftInfo(
			@PathVariable String transferDraftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getTransferDraftDetails(transferDraftID, accessTokenID);
	}

	@RequestMapping(value = "/draft/{transferDraftID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP submitTransferDraftRequest(
			@PathVariable String transferDraftID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.requestOTP(transferDraftID, accessTokenID);
	}

	@RequestMapping(value = "/draft/{transferDraftID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody P2PTransferDraft.Status verifyOTPAndPerformTransferring(
			@PathVariable String transferDraftID,
			@PathVariable String refCode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody OTP otp) {
		if (otp != null) {
			otp.setReferenceCode(refCode);
		}
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.verifyOTP(transferDraftID, otp, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
	public @ResponseBody P2PTransferTransaction.Status getTransferringStatus(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getTransferringStatus(transactionID, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
	public @ResponseBody P2PTransferTransaction getTransactionInfo(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getTransactionResult(transactionID, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
