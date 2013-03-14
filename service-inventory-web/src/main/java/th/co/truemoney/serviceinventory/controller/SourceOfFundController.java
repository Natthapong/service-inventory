package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.apache.log4j.Logger;
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

@Controller
public class SourceOfFundController extends BaseController {
	
	private static Logger logger = Logger.getLogger(SourceOfFundController.class);
	
	@Autowired
	private SourceOfFundService sourceOfFundService;
	
	@RequestMapping(value = "/{username}/source-of-fund/direct-debits", method = RequestMethod.GET)
	public @ResponseBody List<DirectDebit> listDirectDebitSources(
		@PathVariable String username,
		@RequestParam Integer channelID,
		@RequestParam String accessToken)
			throws ServiceInventoryException {
		logger.debug("username : "+ username);
		logger.debug("channelID : "+channelID);
		logger.debug("accessToken: "+accessToken);
		return sourceOfFundService.getDirectDebitSources(channelID, username, accessToken);
	}
	
}
