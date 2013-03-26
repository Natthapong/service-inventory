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
import th.co.truemoney.serviceinventory.exception.ValidateException;
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
		if ("".equals(accessTokenID)) {
			throw new ValidateException("-1",
					"Validate error: accessTokenID is null or empty.");
		}
		return topupService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);
	}
	
	@RequestMapping(value = "/top-up/quote/{quoteID}", method = RequestMethod.GET)
	public @ResponseBody TopUpQuote getQuoteInfo(@PathVariable String quoteID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws SignonServiceException {
		return topupService.getTopUpQuoteDetails(quoteID, accessTokenID);
	}
	
	@RequestMapping(value = "/top-up/order/{quoteID}", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder placeOrder(@PathVariable String quoteID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) 
			throws ServiceInventoryException {		
		return topupService.requestPlaceOrder(quoteID, accessTokenID);
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}/confirm", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder confirmOrder(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
		@RequestBody OTP otp)
			throws ServiceInventoryException {
		if(AccessTokenUtil.isValidCheckSum(otp.getChecksum(), topUpOrderID+otp.getOtpString()+accessTokenID, accessTokenID)) {
			return topupService.confirmPlaceOrder(topUpOrderID, otp, accessTokenID);
		}	else {
			throw new ServiceInventoryException( ServiceInventoryException.Code.INVALID_CHECKSUM, 
					"Invalide Checksum.");
		}		
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}/status", method = RequestMethod.GET)
	public @ResponseBody TopUpStatus getOrderStatus(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {						
		return topupService.getTopUpOrderStatus(topUpOrderID, accessTokenID);
	}
	
	@RequestMapping(value = "/top-up/order/{topUpOrderID}", method = RequestMethod.GET)
	public @ResponseBody TopUpOrder getOrderInfo(@PathVariable String topUpOrderID, 
		@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID)
				throws ServiceInventoryException {
		return topupService.getTopUpOrderDetails(topUpOrderID, accessTokenID);
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
}
