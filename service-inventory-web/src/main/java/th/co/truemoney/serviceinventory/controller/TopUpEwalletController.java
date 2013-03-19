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
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
@RequestMapping(value = "/directdebit")
public class TopUpEwalletController extends BaseController {

	@Autowired
	private TopUpService topupService;

	@RequestMapping(value = "/{sourceOfFundID}/quote", method = RequestMethod.POST)
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
	
	@RequestMapping(value = "/top-up/order/{quoteID}", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder requestPlaceOrder(@PathVariable String quoteID, 
			@RequestParam String accessTokenID) 
		throws ServiceInventoryException {
		
		return null;
	}
}
