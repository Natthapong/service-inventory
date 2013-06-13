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

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
public class TopUpEwalletController {

    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";

    @Autowired
    private TopUpService topupService;

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @RequestMapping(value = "/directdebit/{sourceOfFundID}/quote", method = RequestMethod.POST)
    public @ResponseBody TopUpQuote createTopUpQuoteFromDirectDebit(
           @PathVariable String sourceOfFundID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
           @RequestBody TopUpQuote quote) {


        extendExpireAccessToken(accessTokenID);

        TopUpQuote topUpQuote = topupService.createAndVerifyTopUpQuote(sourceOfFundID, quote.getAmount(), accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, topUpQuote.getID());

        return topUpQuote;
    }

    @RequestMapping(value = "/top-up/quote/{draftTransactionID}", method = RequestMethod.GET)
    public @ResponseBody TopUpQuote getTopUpQuoteDetails(@PathVariable String draftTransactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        extendExpireAccessToken(accessTokenID);

        return topupService.getTopUpQuoteDetails(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/top-up/quote/{draftTransactionID}", method = RequestMethod.PUT)
    public @ResponseBody TopUpOrder.Status performTopUp(
           @PathVariable String draftTransactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        extendExpireAccessToken(accessTokenID);

        return topupService.performTopUp(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/top-up/order/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody TopUpOrder.Status getOrderStatus(
           @PathVariable String transactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return topupService.getTopUpProcessingStatus(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/top-up/order/{transactionID}", method = RequestMethod.GET)
    public @ResponseBody TopUpOrder getOrderInfo(
           @PathVariable String transactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return topupService.getTopUpOrderResults(transactionID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }

}