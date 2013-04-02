package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value="/transfer")
public class P2PTransferController {

	@Autowired
	private P2PTransferService p2pTransferService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/draft-transaction", method = RequestMethod.POST)
	public @ResponseBody P2PDraftTransaction createDraftTransaction(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody P2PDraftTransaction draft) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.createDraftTransaction(draft.getMobileNumber(), draft.getAmount(), accessTokenID);
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}", method = RequestMethod.GET)
	public @ResponseBody P2PDraftTransaction getDraftTransactionInfo(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getDraftTransactionDetails(draftTransactionID, accessTokenID);
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP sendOTP(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.sendOTP(draftTransactionID, accessTokenID);
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody DraftTransaction.Status confirmDraftTransaction(
			@PathVariable String draftTransactionID,
			@PathVariable String refCode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody OTP otp) {
		if (otp != null) {
			otp.setReferenceCode(refCode);
		}
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.confirmDraftTransaction(draftTransactionID, otp, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
	public @ResponseBody Transaction.Status getTransactionStatus(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getTransactionStatus(transactionID, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
	public @ResponseBody P2PTransaction getTransactionInfo(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return p2pTransferService.getTransactionResult(transactionID, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
