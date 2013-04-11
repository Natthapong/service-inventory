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
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
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
	public @ResponseBody BillPaymentInfo getBillInformation(
			@PathVariable String barcode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.getBillInformation(barcode, accessTokenID);
	}
	
	@RequestMapping(value = "/invoice", method = RequestMethod.POST)
	public @ResponseBody
	BillInvoice createDraftTransaction(@RequestParam String accessTokenID,
			@RequestBody BillPaymentInfo billPaymentInfo) {
		extendExpireAccessToken(accessTokenID);
		
		return billPaymentService
				.createBillInvoice(billPaymentInfo, accessTokenID);
	}

	@RequestMapping(value = "/invoice/{invoiceID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody Status confirmOTP(
			@PathVariable String invoiceID,
			@PathVariable String refCode,
			@RequestParam String accessTokenID,
			@RequestBody OTP otp) {

		if (otp != null) {
			otp.setReferenceCode(refCode);
		}

		extendExpireAccessToken(accessTokenID);

		return billPaymentService.confirmBillInvoice(invoiceID, otp, accessTokenID);
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
}
