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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

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
			@RequestBody P2PDraftTransaction draft)
		throws ServiceInventoryException {

		P2PDraftTransaction p2pDraftTransaction = p2pTransferService.createDraftTransaction(draft.getMobileNumber(), draft.getAmount(), accessTokenID);
		extendExpireAccessToken(accessTokenID);

		return p2pDraftTransaction;
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}", method = RequestMethod.POST)
	public @ResponseBody P2PDraftTransaction getDraftTransactionInfo(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
		throws ServiceInventoryException {

		P2PDraftTransaction p2pDraftTransaction = p2pTransferService.getDraftTransactionDetails(draftTransactionID, accessTokenID);
		extendExpireAccessToken(accessTokenID);

		return p2pDraftTransaction;
	}

	@RequestMapping(value = "/draft-transaction/{draftTransactionID}/send-otp", method = RequestMethod.PUT)
	public @ResponseBody OTP sendOTP(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
		throws ServiceInventoryException {
		OTP otp = p2pTransferService.sendOTP(draftTransactionID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return otp;
	}

	@RequestMapping(value = "/transaction/{draftTransactionID}", method = RequestMethod.POST)
	public @ResponseBody P2PTransactionStatus createTransaction(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody OTP otp)
		throws ServiceInventoryException {
		P2PTransactionStatus p2pTransactionStatus = p2pTransferService.createTransaction(draftTransactionID, otp, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return p2pTransactionStatus;
	}

	@RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
	public @ResponseBody P2PTransactionStatus getTransactionStatus(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
		throws ServiceInventoryException {
		P2PTransactionStatus p2pTransactionStatus = p2pTransferService.getTransactionStatus(transactionID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return p2pTransactionStatus;
	}

	@RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
	public @ResponseBody P2PTransaction getTransactionInfo(
			@PathVariable String transactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
		throws ServiceInventoryException {
		P2PTransaction p2pTransaction = p2pTransferService.getTransactionResult(transactionID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return p2pTransaction;
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
