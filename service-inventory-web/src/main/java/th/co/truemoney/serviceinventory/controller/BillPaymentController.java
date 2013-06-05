package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController {

	private static final Logger logger = LoggerFactory.getLogger(BillPaymentController.class);
	
	@Autowired
	private BillPaymentService billPaymentService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/information", method = RequestMethod.GET, params="barcode")
	public @ResponseBody Bill scanAndCreateBillPayment(
			@RequestParam(value = "barcode", defaultValue = "") String barcode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);
	}
	
	@RequestMapping(value = "/information", method = RequestMethod.GET, params="billCode")
	public @ResponseBody Bill getBillInformation(
			@RequestParam(value = "billCode", defaultValue = "") String billCode,
			@RequestParam(value = "ref1", defaultValue = "") String ref1,
			@RequestParam(value = "amount", defaultValue = "") String amount,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.retrieveBillInformationWithBillCode(billCode, ref1, new BigDecimal(amount), accessTokenID);
	} 
	
	@RequestMapping(value = "/information/{billCode}", method = RequestMethod.GET)
	public @ResponseBody Bill getBillInformation(
			@PathVariable String billCode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.retrieveBillInformationWithKeyin(billCode, accessTokenID);
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
	
	@RequestMapping(value = "/information", method = RequestMethod.POST)
	public @ResponseBody Bill updateBillInformation(
			@RequestParam(value = "billID", defaultValue = "") String billID,
			@RequestParam(value = "ref1", defaultValue = "") String ref1,
			@RequestParam(value = "ref2", defaultValue = "") String ref2,
			@RequestParam(value = "amount", defaultValue = "") String amount,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		logger.debug("billID : "+billID);
		logger.debug("ref1 : "+ref1);
		logger.debug("ref2 : "+ref2);
		logger.debug("amount : "+amount);
		logger.debug("accessTokenID : "+accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.updateBillInformation(billID, ref1, ref2, new BigDecimal(amount), accessTokenID);
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
