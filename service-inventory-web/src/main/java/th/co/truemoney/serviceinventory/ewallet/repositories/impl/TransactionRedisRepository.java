package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionRedisRepository implements TransactionRepository {

	private static Logger logger = LoggerFactory.getLogger(TransactionRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveDraftTransaction(DraftTransaction draft, String accessTokenID) {
		String key = generateDraftKey(draft.getID(), accessTokenID);
		try {
			redisLoggingDao.addData(key, mapper.writeValueAsString(draft), 20L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public <T extends DraftTransaction> T findDraftTransaction(String draftID, String accessTokenID,  Class<T> clazz) {
		try {
			String key = generateDraftKey(draftID, accessTokenID);
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "draft transaction not found: " + key);
			}
			return mapper.readValue(result, clazz);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveTransaction(Transaction transaction, String accessTokenID) {
		try {
			redisLoggingDao.addData(generateTransactionKey(transaction.getID(), accessTokenID), mapper.writeValueAsString(transaction), 20L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public <T extends Transaction> T findTransaction(String transactionID,
			String accessTokenID, Class<T> clazz) {
		try {
			String key = generateTransactionKey(transactionID, accessTokenID);
			String result = redisLoggingDao.getData(key);

			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "Transaction not found: " + key);
			}

			return mapper.readValue(result, clazz);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	private String generateDraftKey(String draftID, String accessTokenID) {
		return "draft:" + draftID + ":" + accessTokenID;
	}

	private String generateTransactionKey(String transactionID, String accessTokenID) {
		return "transaction:" + transactionID + ":" + accessTokenID;
	}

}
