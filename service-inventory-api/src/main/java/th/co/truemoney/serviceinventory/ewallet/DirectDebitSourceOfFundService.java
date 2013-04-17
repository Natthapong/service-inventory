package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface DirectDebitSourceOfFundService {
	public List<DirectDebit> getUserDirectDebitSources(String accessTokenID) throws ServiceInventoryException;
}
