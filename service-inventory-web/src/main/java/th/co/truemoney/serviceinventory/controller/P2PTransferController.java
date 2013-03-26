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
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
@RequestMapping(value="/transfer")
public class P2PTransferController extends BaseController {

	@Autowired
	private P2PTransferService p2pTransferService;
	
	@Autowired 
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/draft-transaction", method = RequestMethod.POST)
	public @ResponseBody P2PDraftTransaction createDraftTransaction(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody P2PDraftRequest p2pDraftRequest)
		throws ServiceInventoryException {
		P2PDraftTransaction p2pDraftTransaction = p2pTransferService.createDraftTransaction(p2pDraftRequest, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return p2pDraftTransaction;
	}
	
	@RequestMapping(value = "/draft-transaction/{draftTransactionID}", method = RequestMethod.POST)
	public @ResponseBody P2PDraftTransaction getDraftTransactionInfo(
			@PathVariable String draftTransactionID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
		throws ServiceInventoryException {
		P2PDraftTransaction p2pDraftTransaction = p2pTransferService.getDraftTransactionDetail(draftTransactionID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return p2pDraftTransaction;
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
}
