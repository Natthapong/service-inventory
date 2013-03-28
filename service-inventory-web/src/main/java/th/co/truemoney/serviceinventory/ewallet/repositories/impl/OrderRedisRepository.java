package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderRedisRepository implements OrderRepository {

	private static Logger logger = LoggerFactory.getLogger(OrderRedisRepository.class);

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote) {
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
	public TopUpQuote getTopUpQuote(String orderID) {
		try {
			String result = redisLoggingDao.getData("quote:"+orderID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.TRANSACTION_NOT_FOUND,
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
	public void saveTopUpOrder(TopUpOrder topupOrder) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("order:"+topupOrder.getID(), mapper.writeValueAsString(topupOrder), 15L);
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public TopUpOrder getTopUpOrder(String orderID) throws ServiceInventoryException {
		try {
			String result = redisLoggingDao.getData("order:"+orderID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.TRANSACTION_NOT_FOUND,
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

	@Override
	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("p2pdraft:"+p2pDraftTransaction.getID(), mapper.writeValueAsString(p2pDraftTransaction), 15L);
		} catch (Exception e) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
		}
	}

	@Override
	public P2PDraftTransaction getP2PDraftTransaction(
			String p2pDraftTransactionID) {
		try {
			String result = redisLoggingDao.getData("p2pdraft:"+p2pDraftTransactionID);
			if(result == null) {
				throw new ServiceInventoryException(ServiceInventoryException.Code.DRAFT_TRANSACTION_NOT_FOUND,
						"P2P draft transaction not found.");
			}
			ObjectMapper mapper = new ObjectMapper();
			
			return mapper.readValue(result, P2PDraftTransaction.class);
		} catch (ServiceInventoryException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}

}
