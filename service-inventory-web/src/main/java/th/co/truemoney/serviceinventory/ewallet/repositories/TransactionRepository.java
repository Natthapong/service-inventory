package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;

public interface TransactionRepository {
	public void saveDraftTransaction(DraftTransaction draft, String accessTokenID);
	public <T extends DraftTransaction> T findDraftTransaction(String draftID, String accessTokenID, Class<T> clazz);

	public void saveTransaction(Transaction transaction, String accessTokenID);
	public <T extends Transaction> T findTransaction(String transactionID, String accessTokenID, Class<T> clazz);
}
