package th.co.truemoney.serviceinventory.controller;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@Controller
@RequestMapping(value="/transfer")
public class P2PTransferController {

    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";

    @Autowired
    private P2PTransferService p2pTransferService;

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public @ResponseBody P2PTransferDraft createTransferDraft(
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
            @RequestBody P2PTransferDraft draft) {

        extendExpireAccessToken(accessTokenID);

        P2PTransferDraft transferDraft = p2pTransferService.createAndVerifyTransferDraft(draft.getMobileNumber(), draft.getAmount(), accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, transferDraft.getID());

        return transferDraft;
    }

    @RequestMapping(value = "/draft/{draftTransactionID}/update", method = RequestMethod.PUT)
    public @ResponseBody void setPersonalMessage(
            @PathVariable String draftTransactionID,
            @RequestParam(value = "personalMessage", defaultValue = "") String personalMessage,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        extendExpireAccessToken(accessTokenID);

        p2pTransferService.setPersonalMessage(draftTransactionID, personalMessage, accessTokenID);
    }

    @RequestMapping(value = "/draft/{draftTransactionID}", method = RequestMethod.GET)
    public @ResponseBody P2PTransferDraft getTransferDraftInfo(
            @PathVariable String draftTransactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        extendExpireAccessToken(accessTokenID);

        return p2pTransferService.getTransferDraftDetails(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.PUT)
    public @ResponseBody P2PTransferTransaction.Status performTransfer(
	    @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

	MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

	return p2pTransferService.performTransfer(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody P2PTransferTransaction.Status getTransferringStatus(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return p2pTransferService.getTransferringStatus(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
    public @ResponseBody P2PTransferTransaction getTransactionInfo(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return p2pTransferService.getTransactionResult(transactionID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }

}
