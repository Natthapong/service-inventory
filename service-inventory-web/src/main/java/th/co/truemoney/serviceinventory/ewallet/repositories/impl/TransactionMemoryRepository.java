package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class TransactionMemoryRepository implements TransactionRepository {

	private HashMap<String, DraftTransaction> draftsMap = new LinkedHashMap<String, DraftTransaction>();
	private HashMap<String, Transaction> transactionMap = new LinkedHashMap<String, Transaction>();

	@Override
	public void saveDraftTransaction(DraftTransaction draft, String accessTokenID) {
		String key = generateDraftKey(draft.getID(), accessTokenID);
		draftsMap.put(key, draft);
	}

	@Override
	public <T extends DraftTransaction> T findDraftTransaction(String draftID, String accessTokenID,  Class<T> clazz) {
		DraftTransaction draft = draftsMap.get(generateDraftKey(draftID, accessTokenID));

		if (draft == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND, "draft transaction not found.");
		}

		return (T) draft;
	}

	@Override
	public void saveTransaction(Transaction transaction, String accessTokenID) {
		String key = generateTransactionKey(transaction.getID(), accessTokenID);
		transactionMap.put(key, transaction);
	}

	@Override
	public <T extends Transaction> T findTransaction(String transactionID,
			String accessTokenID, Class<T> clazz) {

		Transaction transaction = transactionMap.get(generateTransactionKey(transactionID, accessTokenID));

		if (transaction == null) {
			throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "Transaction not found.");
		}

		return (T) transaction;
	}

	private String generateDraftKey(String draftID, String accessTokenID) {
		return "draft:" + draftID + ":" + accessTokenID;
	}

	private String generateTransactionKey(String transactionID, String accessTokenID) {
		return "transaction:" + transactionID + ":" + accessTokenID;
	}

	public void clear() {
		draftsMap.clear();
		transactionMap.clear();
	}

}
