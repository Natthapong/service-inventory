package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
@RequestMapping(value = "/billpay")
public class BillPayController {

	@Autowired
	BillPaymentService billPaymentService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/invoice/{draftTransactionID}/send-otp/{accessTokenID}", method = RequestMethod.POST)
	public @ResponseBody
	BillInvoice createDraftTransaction(@PathVariable String draftTransactionID,
			@PathVariable String accessTokenID,
			@RequestBody BillPaymentInfo billPaymentInfo) {
		extendExpireAccessToken(accessTokenID);
		
		return billPaymentService
				.createBillInvoice(billPaymentInfo, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
}
