package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface EnhancedDirectDebitSourceOfFundService extends DirectDebitSourceOfFundService {
	public DirectDebit getUserDirectDebitSource(String sourceOfFundID, String accessTokenID) throws ServiceInventoryException;
	public BigDecimal calculateTopUpFee(BigDecimal amount, DirectDebit sofDetail) throws ServiceInventoryException;
}
