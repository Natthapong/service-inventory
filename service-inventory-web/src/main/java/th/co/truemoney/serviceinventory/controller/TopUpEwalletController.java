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
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.util.AccessTokenUtil;

@Controller
public class TopUpEwalletController extends BaseController {

	@Autowired
	private TopUpService topupService;
	
	@Autowired 
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/directdebit/{sourceOfFundID}/quote", method = RequestMethod.POST)
	public @ResponseBody
	TopUpQuote createTopUpQuoteFromDirectDebit(
			@PathVariable String sourceOfFundID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody QuoteRequest quoteRequest)
			throws ServiceInventoryException {
		TopUpQuote topUpQuote = topupService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpQuote;
	}
	
	@RequestMapping(value = "/top-up/quote/{quoteID}", method = RequestMethod.GET)
	public @ResponseBody TopUpQuote getQuoteInfo(@PathVariable String quoteID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws SignonServiceException {
		TopUpQuote topUpQuote = topupService.getTopUpQuoteDetails(quoteID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpQuote;
	}
	
	@RequestMapping(value = "/top-up/order/{quoteID}", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder placeOrder(@PathVariable String quoteID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) 
			throws ServiceInventoryException {		
		TopUpOrder topUpOrder = topupService.requestPlaceOrder(quoteID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpOrder;
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}/confirm", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder confirmOrder(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
		@RequestBody OTP otp)
			throws ServiceInventoryException {
		if(AccessTokenUtil.isValidCheckSum(otp.getChecksum(), topUpOrderID+otp.getOtpString()+accessTokenID, accessTokenID)) {
			TopUpOrder topUpOrder = topupService.confirmPlaceOrder(topUpOrderID, otp, accessTokenID);
			extendExpireAccessToken(accessTokenID);
			return topUpOrder;
		}	else {
			throw new ServiceInventoryException( ServiceInventoryException.Code.INVALID_CHECKSUM, 
					"Invalide Checksum.");
		}		
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}/status", method = RequestMethod.GET)
	public @ResponseBody TopUpStatus getOrderStatus(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {						
		TopUpStatus topUpStatus = topupService.getTopUpOrderStatus(topUpOrderID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpStatus;
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}", method = RequestMethod.GET)
	public @ResponseBody TopUpOrder getOrderInfo(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {
		TopUpOrder topUpOrder = topupService.getTopUpOrderDetails(topUpOrderID, accessTokenID);
		extendExpireAccessToken(accessTokenID);
		return topUpOrder;
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
}
