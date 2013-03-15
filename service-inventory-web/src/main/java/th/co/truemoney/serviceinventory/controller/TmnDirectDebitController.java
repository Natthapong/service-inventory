package th.co.truemoney.serviceinventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.TmnDirectDebitService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrderResult;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
@RequestMapping(value = "/directdebit")
public class TmnDirectDebitController extends BaseController {

	private TmnDirectDebitService tmnDirectDebitService;

	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public @ResponseBody
	DirectDebitOrderResult verify(
			@RequestParam(value = "channelID", defaultValue = "-1") Integer channelID,
			@RequestParam(value = "accessTokenID", defaultValue = "") String accessTokenID,
			@RequestBody DirectDebitOrder directDebitOrder)
			throws ServiceInventoryException {
		
		if (channelID == -1 || "".equals(accessTokenID)) {
			throw new ValidateException("-1",
					"Validate error: channelId is null or empty.");
		}

		return tmnDirectDebitService.verify(channelID, accessTokenID, directDebitOrder);
	}
}
