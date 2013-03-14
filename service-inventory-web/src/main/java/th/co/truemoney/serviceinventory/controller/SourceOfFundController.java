package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Controller
public class SourceOfFundController {
	
	@RequestMapping(value = "/{username}/source-of-fund/direct-debits", method = RequestMethod.GET)
	public @ResponseBody List<DirectDebit> listDirectDebitSources(
		@RequestParam Integer channelID,
		@RequestParam String accesstoken)
			throws ServiceInventoryException {
		return null;
	}
	
}
