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
public class TopUpEwalletController extends BaseController {

	@Autowired
	private TopUpService topupService;

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
		
		//--- checksum data ---//
		/*
		String checksum = quoteRequest.getChecksum();
		String datasum = String.format("%s%s", sourceOfFundID, quoteRequest.getAmount().toString());
		if (!AccessTokenUtil.isValidCheckSum(checksum, datasum, accessTokenId))
			throw new ValidateException("-1",
					"Validate error: invalid checksum.");
		*/
		
		return topupService.createTopUpQuoteFromDirectDebit(sourceOfFundID, quoteRequest, accessTokenID);
	}
	
	@RequestMapping(value = "/top-up/order/{quoteID}", method = RequestMethod.POST)
	public @ResponseBody TopUpOrder requestPlaceOrder(@PathVariable String quoteID, 
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID) 
		throws ServiceInventoryException {		
		return topupService.requestPlaceOrder(quoteID, accessTokenID);
	}
	
}
