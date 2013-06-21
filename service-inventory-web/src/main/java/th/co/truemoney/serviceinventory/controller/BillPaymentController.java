package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController {

    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";

    @Autowired
    private BillPaymentService billPaymentService;

    @Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynService;

    @RequestMapping(value = "/information", method = RequestMethod.GET, params="barcode")
    public @ResponseBody Bill getBillInfoFromBarcode(
            @RequestParam(value = "barcode", defaultValue = "") String barcode,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "inquiry=online"})
    public @ResponseBody Bill getKeyInBillInfoOnline(
            @RequestParam(value = "billCode", defaultValue = "") String billCode,
            @RequestParam(value = "ref1", defaultValue = "") String ref1,
            @RequestParam(value = "ref2", defaultValue = "") String ref2,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillInformationWithKeyin(billCode, ref1, ref2, BigDecimal.ZERO, InquiryOutstandingBillType.ONLINE, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "inquiry=offline"})
    public @ResponseBody Bill getKeyInBillInfoOffline(
            @RequestParam(value = "billCode", defaultValue = "") String billCode,
            @RequestParam(value = "ref1", defaultValue = "") String ref1,
            @RequestParam(value = "ref2", defaultValue = "") String ref2,
            @RequestParam(value = "amount", defaultValue = "0") String amount,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillInformationWithKeyin(billCode, ref1, ref2, new BigDecimal(amount), InquiryOutstandingBillType.OFFLINE, accessTokenID);
    }
    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "inquiry=online", "favorite=true"})
    public @ResponseBody Bill getFavouriteBillInfoOnline(
            @RequestParam(value = "billCode", defaultValue = "") String billCode,
            @RequestParam(value = "ref1", defaultValue = "") String ref1,
            @RequestParam(value = "ref2", defaultValue = "") String ref2,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillInformationWithUserFavorite(billCode, ref1, ref2, BigDecimal.ZERO, InquiryOutstandingBillType.ONLINE, accessTokenID);
    }

    @RequestMapping(value = "/information", method = RequestMethod.GET, params={"billCode", "inquiry=offline", "favorite=true"})
    public @ResponseBody Bill getFavouriteBillInfoOffline(
            @RequestParam(value = "billCode", defaultValue = "") String billCode,
            @RequestParam(value = "ref1", defaultValue = "") String ref1,
            @RequestParam(value = "ref2", defaultValue = "") String ref2,
            @RequestParam(value = "amount", defaultValue = "0") String amount,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillInformationWithUserFavorite(billCode, ref1, ref2, new BigDecimal(amount), InquiryOutstandingBillType.OFFLINE, accessTokenID);
    }

    @RequestMapping(value = "/information/outstanding/{billCode}/{ref1}", method = RequestMethod.GET)
    public @ResponseBody OutStandingBill getBillOutStandingOnline(
            @PathVariable String billCode,
            @PathVariable String ref1,
            @RequestParam(value = "ref2", defaultValue = "") String ref2,
            @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
        extendExpireAccessToken(accessTokenID);
        return billPaymentService.retrieveBillOutStandingOnline(billCode, ref1, ref2, accessTokenID);
    }

    @RequestMapping(value = "/invoice/{billInfoID}", method = RequestMethod.POST)
    public @ResponseBody
    BillPaymentDraft verifyPaymentAbility(
            @PathVariable String billInfoID,
            @RequestParam String accessTokenID,
            @RequestBody BillPaymentDraft billPaymentInfo) {

        extendExpireAccessToken(accessTokenID);

        BillPaymentDraft draft = billPaymentService.verifyPaymentAbility(billInfoID, billPaymentInfo.getAmount(), accessTokenID);

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draft.getID());

        return draft;
    }

    @RequestMapping(value = "/invoice/{draftTransactionID}", method = RequestMethod.GET)
    public @ResponseBody BillPaymentDraft getDraftDetail(
           @PathVariable String draftTransactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_DRAFT_TRANSACTION_ID, draftTransactionID);

        extendExpireAccessToken(accessTokenID);

        return billPaymentService.getBillPaymentDraftDetail(draftTransactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.PUT)
    public @ResponseBody Transaction.Status performPayment(
	    @PathVariable String transactionID,
            @RequestParam String accessTokenID) {

	MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

	return billPaymentService.performPayment(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody BillPaymentTransaction.Status getPaymentStatus(
           @PathVariable String transactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return billPaymentService.getBillPaymentStatus(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}", method = RequestMethod.GET)
    public @ResponseBody BillPaymentTransaction getPaymentResult(
           @PathVariable String transactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        MDC.put(MDC_TRANSACTION_ID, transactionID);

        extendExpireAccessToken(accessTokenID);

        return billPaymentService.getBillPaymentResult(transactionID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }


}
