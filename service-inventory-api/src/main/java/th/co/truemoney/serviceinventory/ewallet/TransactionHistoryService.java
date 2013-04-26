package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.TransactionHistory;
import th.co.truemoney.serviceinventory.ewallet.domain.TransactionHistoryDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TransactionHistoryService {
	
	public List<TransactionHistory> getTransactions(String accessTokenID)
		throws ServiceInventoryException;
	
	public TransactionHistoryDetail getTransactionDetail(String reportID, String accessTokenID)
		throws ServiceInventoryException;
	
}
