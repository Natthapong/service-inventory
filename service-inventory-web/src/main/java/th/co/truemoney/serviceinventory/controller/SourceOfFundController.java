package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;

@Controller
public class SourceOfFundController {

	@Autowired
	private DirectDebitSourceOfFundService sourceOfFundService;

	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;

	@RequestMapping(value = "/ewallet/profile/source-of-fund/direct-debits", method = RequestMethod.GET)
	public @ResponseBody DirectDebit[] listDirectDebitSources(
			@RequestParam(value = "accessTokenID", defaultValue="") String accessTokenID) {
		List<DirectDebit> directDebits = sourceOfFundService.getUserDirectDebitSources(accessTokenID);
		extendExpireAccessToken(accessTokenID);

		return directDebits.toArray(new DirectDebit[directDebits.size()]);
	}

	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}

}
