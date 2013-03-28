package th.co.truemoney.serviceinventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrderStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuoteStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@Controller
public class TopUpEwalletController {

	@Autowired
	private TopUpService topupService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/directdebit/{sourceOfFundID}/quote", method = RequestMethod.POST)
	public @ResponseBody
	TopUpQuote createTopUpQuoteFromDirectDebit(
			@PathVariable String sourceOfFundID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody TopUpQuote quote)
			throws ServiceInventoryException {

		TopUpQuote topUpQuote = topupService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quote.getAmount(), accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpQuote;
	}

	@RequestMapping(value = "/top-up/quote/{quoteID}", method = RequestMethod.GET)
	public @ResponseBody TopUpQuote getTopUpQuoteDetails(@PathVariable String quoteID,
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws SignonServiceException {

		extendExpireAccessToken(accessTokenID);
		return topupService.getTopUpQuoteDetails(quoteID, accessTokenID);
	}

	@RequestMapping(value = "/top-up/quote/{quoteID}/otp", method = RequestMethod.POST)
	public @ResponseBody OTP sendOTPConfirm(@PathVariable String quoteID,
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) throws ServiceInventoryException {

		extendExpireAccessToken(accessTokenID);
		return topupService.sendOTPConfirm(quoteID, accessTokenID);
	}

	@RequestMapping(value = "/top-up/quote/{quoteID}/otp/{refCode}", method = RequestMethod.PUT)
	public @ResponseBody TopUpQuoteStatus confirmOrder(
		@PathVariable String quoteID,
        @PathVariable String refCode,
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
		@RequestBody OTP otp)
			throws ServiceInventoryException {

		if (otp != null) {
			otp.setReferenceCode(refCode);
		}

		extendExpireAccessToken(accessTokenID);

		return topupService.confirmOTP(quoteID, otp, accessTokenID);
	}

	@RequestMapping(value = "/top-up/order/{orderID}/status", method = RequestMethod.GET)
	public @ResponseBody TopUpOrderStatus getOrderStatus(@PathVariable String orderID,
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {

		extendExpireAccessToken(accessTokenID);

		return topupService.getTopUpProcessingStatus(orderID, accessTokenID);
	}

	@RequestMapping(value = "/top-up/order/{orderID}", method = RequestMethod.GET)
	public @ResponseBody TopUpOrder getOrderInfo(@PathVariable String orderID,
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {

		return topupService.getTopUpOrderResults(orderID, accessTokenID);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
