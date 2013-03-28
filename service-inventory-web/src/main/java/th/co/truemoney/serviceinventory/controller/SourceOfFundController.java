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
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class SourceOfFundController {

	@Autowired
	private SourceOfFundService sourceOfFundService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/user/{username}/source-of-fund/direct-debits", method = RequestMethod.GET)
	public @ResponseBody DirectDebit[] listDirectDebitSources(
		@PathVariable String username,
		@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID)
			throws ServiceInventoryException {
		List<DirectDebit> directDebits = sourceOfFundService.getUserDirectDebitSources(username, accessTokenID);
		extendExpireAccessToken(accessTokenID);

		return directDebits.toArray(new DirectDebit[directDebits.size()]);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
