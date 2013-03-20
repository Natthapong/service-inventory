package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class OrderRedisRepository implements OrderRepository {

	private static Logger logger = Logger.getLogger(OrderRedisRepository.class);
	
	@Autowired
	private RedisLoggingDao redisLoggingDao;
	
	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("quote:"+topupQuote.getID(), mapper.writeValueAsString(topupQuote), 15L);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public TopUpQuote getTopUpQuote(String orderID) {
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
			logger.error(e);			
		}
		return null;		
	}
	
	@Override
	public void saveTopUpOrder(TopUpOrder topupOrder) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			redisLoggingDao.addData("order:"+topupOrder.getID(), mapper.writeValueAsString(topupOrder), 15L);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public TopUpOrder getTopUpOrder(String orderID) throws ServiceInventoryException {
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
			logger.error(e);			
		}
		return null;	
	}

}
