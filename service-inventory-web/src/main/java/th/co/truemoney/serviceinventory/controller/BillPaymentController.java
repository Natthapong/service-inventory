package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController {

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

    @RequestMapping(value = "/invoice/{invoiceID}", method = RequestMethod.POST)
    public @ResponseBody
    BillPaymentDraft verifyPaymentAbility(
            @PathVariable String invoiceID,
            @RequestParam String accessTokenID,
            @RequestBody BillPaymentDraft billPaymentInfo) {
        extendExpireAccessToken(accessTokenID);

        return billPaymentService.verifyPaymentAbility(invoiceID, billPaymentInfo.getAmount(), accessTokenID);
    }

    @RequestMapping(value = "/invoice/{invoiceID}", method = RequestMethod.GET)
    public @ResponseBody BillPaymentDraft getBillDetails(
           @PathVariable String invoiceID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return billPaymentService.getBillPaymentDraftDetail(invoiceID, accessTokenID);
    }

    @RequestMapping(value = "/invoice/{invoiceID}", method = RequestMethod.PUT)
    public @ResponseBody BillPaymentTransaction.Status performPayment(
            @PathVariable String invoiceID,
            @RequestParam String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return billPaymentService.performPayment(invoiceID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{transactionID}/status", method = RequestMethod.GET)
    public @ResponseBody BillPaymentTransaction.Status getPaymentStatus(
           @PathVariable String transactionID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);

        return billPaymentService.getBillPaymentStatus(transactionID, accessTokenID);
    }

    @RequestMapping(value = "/transaction/{billPaymentID}", method = RequestMethod.GET)
    public @ResponseBody BillPaymentTransaction getPaymentResult(
           @PathVariable String billPaymentID,
           @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

        extendExpireAccessToken(accessTokenID);
        return billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);
    }

    private void extendExpireAccessToken(String accessTokenID) {
        extendAccessTokenAsynService.setExpire(accessTokenID);
    }


}
