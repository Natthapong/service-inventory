package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ValidateException;

@Controller
public class SourceOfFundController extends BaseController {

	@Autowired
	private SourceOfFundService sourceOfFundService;

	@RequestMapping(value = "/user/{username}/source-of-fund/direct-debits", method = RequestMethod.GET)
	public @ResponseBody List<DirectDebit> listDirectDebitSources(
		@PathVariable String username,
		@RequestParam(value = "channelId", defaultValue="-1") Integer channelId,
		@RequestParam(value = "accessToken", defaultValue="") String accessToken)
			throws ServiceInventoryException {
		if (channelId == -1) {
			throw new ValidateException("-1", "Validate error: channelId is null or empty.");
		} else if (accessToken == null || accessToken.equals("")) {
			throw new ValidateException("-1", "Validate error: accessToken is null or empty.");
		}
		return sourceOfFundService.getDirectDebitSources(channelId, username, accessToken);
	}

}
