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
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Controller
@RequestMapping(value = "/top-up/mobile")
public class TopUpMobileController {

    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @Autowired
    private TopUpMobileService topUpMobileService;


    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public @ResponseBody TopUpMobileDraft verifyAndCreateTopupMobileDraft(
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
            @RequestBody TopUpMobileDraft draft) {

        extendExpireAccessToken(accessTokenID);

        TopUpMobile topUpInfo = draft.getTopUpMobileInfo();

        TopUpMobileDraft topUpDraft = topUpMobileService.verifyAndCreateTopUpMobileDraft(topUpInfo.getMobileNumber(), topUpInfo.getAmount(), accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, topUpDraft.getID());

        return topUpDraft;
    }

    @RequestMapping(value = "/draft/{draftTransactionID}", method = RequestMethod.GET)
    public @ResponseBody TopUpMobileDraft getTopupMobileDraftDetail(
            @PathVariable String draftTransactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        return topUpMobileService.getTopUpMobileDraftDetail(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}",  method = RequestMethod.PUT)
    public @ResponseBody TopUpMobileTransaction.Status performTopupMobile(
    		@PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

		MDC.put(MDC_TRANSACTION_ID, transactionID);
	
		return topUpMobileService.performTopUpMobile(transactionID, accessTokenID);
    }


    @RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody TopUpMobileTransaction.Status getTopupMobileStatus(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        return topUpMobileService.getTopUpMobileStatus(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
    public @ResponseBody TopUpMobileTransaction getTopupMobileInfo(
            @PathVariable String transactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        return topUpMobileService.getTopUpMobileResult(transactionID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }

}
