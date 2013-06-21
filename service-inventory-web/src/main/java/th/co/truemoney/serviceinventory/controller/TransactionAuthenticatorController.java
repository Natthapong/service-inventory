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

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/authen")
public class TransactionAuthenticatorController {

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @Autowired
    private TransactionAuthenService transactionAuthenService;

    @RequestMapping(value = "/draft/{draftTransactionID}/otp", method = RequestMethod.POST)
    public @ResponseBody OTP submitTopUpMobileDraftRequest(
            @PathVariable String draftTransactionID,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        return transactionAuthenService.requestOTP(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/draft/{draftTransactionID}/otp/{refCode}", method = RequestMethod.PUT)
    public @ResponseBody DraftTransaction.Status verifyOTPToppingMobile(
            @PathVariable String draftTransactionID,
            @PathVariable String refCode,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
            @RequestBody OTP otp) {

        if (otp != null) {
            otp.setReferenceCode(refCode);
        }

        extendExpireAccessToken(accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        return transactionAuthenService.verifyOTP(draftTransactionID, otp, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }

}
