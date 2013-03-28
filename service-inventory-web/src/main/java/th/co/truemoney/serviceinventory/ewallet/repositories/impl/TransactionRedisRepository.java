package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionRedisRepository implements TransactionRepository {

	private static Logger logger = LoggerFactory.getLogger(TransactionRedisRepository.class);

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("quote:"+topupQuote.getID(), mapper.writeValueAsString(topupQuote), 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID) {
		try {
			String result = redisLoggingDao.getData("quote:"+orderID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_ORDER_NOT_FOUND,
						"qoute not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, TopUpQuote.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("order:"+topupOrder.getID(), mapper.writeValueAsString(topupOrder), 15L);
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public TopUpOrder getTopUpEwalletTransaction(String orderID) throws ServiceInventoryException {
		try {
			String result = redisLoggingDao.getData("order:"+orderID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.TOPUP_ORDER_NOT_FOUND,
						"TopUp Ewallet order not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(result, TopUpOrder.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
