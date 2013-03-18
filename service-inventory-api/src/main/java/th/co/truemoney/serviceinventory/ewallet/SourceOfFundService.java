package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;

public interface SourceOfFundService {

	public List<DirectDebit> getUserDirectDebitSources(String username, String accessToken);
}
