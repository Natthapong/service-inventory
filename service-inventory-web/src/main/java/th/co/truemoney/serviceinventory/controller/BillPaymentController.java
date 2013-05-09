package th.co.truemoney.serviceinventory.controller;

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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController {

	@Autowired
	BillPaymentService billPaymentService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/barcode/{barcode}", method = RequestMethod.GET)
	public @ResponseBody Bill scanAndCreateBillPayment(
			@PathVariable String barcode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.retrieveBillInformation(barcode, accessTokenID);
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

	@RequestMapping(value = "/invoice/{invoiceID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP requestOTP(
		   @PathVariable String invoiceID,
		   @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		return billPaymentService.requestOTP(invoiceID, accessTokenID);
	}


	@RequestMapping(value = "/invoice/{invoiceID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody BillPaymentDraft.Status verifyOTP(
			@PathVariable String invoiceID,
			@PathVariable String refCode,
			@RequestParam String accessTokenID,
			@RequestBody OTP otp) {

		if (otp != null) {
			otp.setReferenceCode(refCode);
		}

		extendExpireAccessToken(accessTokenID);

		return billPaymentService.verifyOTP(invoiceID, otp, accessTokenID);
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
