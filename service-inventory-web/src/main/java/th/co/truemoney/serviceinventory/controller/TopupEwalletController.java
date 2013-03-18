package th.co.truemoney.serviceinventory.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
@RequestMapping(value = "/directdebit")
public class TopupEwalletController extends BaseController {

	private TopUpService topupService;

	@RequestMapping(value = "/quote", method = RequestMethod.POST)
	public @ResponseBody
	TopUpQuote verify(
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody String sourceOfFundID,
			@RequestBody BigDecimal amount)
			throws ServiceInventoryException {
		
		
		if ("".equals(accessTokenID)){
			throw new ValidateException("-1",
					"Validate error: accessTokenID is null or empty.");
		}

		System.out.println(sourceOfFundID);
		System.out.println(amount);
		return null;
		//return topupService.createTopUpQuoteFromDirectDebit(sourceOfFundID, amount, accessTokenID);
	}
}
