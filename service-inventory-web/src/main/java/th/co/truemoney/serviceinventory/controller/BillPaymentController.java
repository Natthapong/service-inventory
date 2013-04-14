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
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
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
	public @ResponseBody BillInfo getBillInformation(
			@PathVariable String barcode,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		return billPaymentService.getBillInformation(barcode, accessTokenID);
	}

	@RequestMapping(value = "/invoice", method = RequestMethod.POST)
	public @ResponseBody
	Bill createBill(@RequestParam String accessTokenID,
			@RequestBody BillInfo billPaymentInfo) {
		extendExpireAccessToken(accessTokenID);

		return billPaymentService
				.createBill(billPaymentInfo, accessTokenID);
	}

	@RequestMapping(value = "/invoice/{billID}", method = RequestMethod.GET)
	public @ResponseBody Bill getBillDetails(@PathVariable String billID,
		   @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		return billPaymentService.getBillDetail(billID, accessTokenID);
	}

	@RequestMapping(value = "/invoice/{invoiceID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP submitTopUpRequest(
		   @PathVariable String invoiceID,
		   @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		return billPaymentService.sendOTP(invoiceID, accessTokenID);
	}


	@RequestMapping(value = "/invoice/{billID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody Bill.Status confirmBill(
			@PathVariable String billID,
			@PathVariable String refCode,
			@RequestParam String accessTokenID,
			@RequestBody OTP otp) {

		if (otp != null) {
			otp.setReferenceCode(refCode);
		}

		extendExpireAccessToken(accessTokenID);

		return billPaymentService.confirmBill(billID, otp, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{billPaymentID}/status", method = RequestMethod.GET)
	public @ResponseBody BillPayment.Status getPaymentStatus(
		   @PathVariable String billPaymentID,
		   @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);

		return billPaymentService.getBillPaymentStatus(billPaymentID, accessTokenID);
	}

	@RequestMapping(value = "/transaction/{billPaymentID}", method = RequestMethod.GET)
	public @ResponseBody BillPayment getPaymentResult(
		   @PathVariable String billPaymentID,
		   @RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) {

		extendExpireAccessToken(accessTokenID);
		return billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
