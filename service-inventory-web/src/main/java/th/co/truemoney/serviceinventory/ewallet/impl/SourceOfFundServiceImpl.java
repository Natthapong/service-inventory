package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;

public class SourceOfFundServiceImpl implements SourceOfFundService {

	@Override
	public List<DirectDebit> getDirectDebitSources(Integer channelId, String username, String accessToken) {
		return null;
	}

}
