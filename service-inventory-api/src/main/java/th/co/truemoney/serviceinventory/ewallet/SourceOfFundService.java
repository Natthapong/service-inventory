package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface SourceOfFundService {

	public List<DirectDebit> getUserDirectDebitSources(String username, String accessToken) throws ServiceInventoryException;
}
