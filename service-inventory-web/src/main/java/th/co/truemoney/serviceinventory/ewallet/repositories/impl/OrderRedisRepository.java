package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@SuppressWarnings({"unchecked", "rawtypes"})
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
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");	
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

			Map<String,Object> hashMap = mapper.readValue(result, HashMap.class);
			
			TopUpQuote topUpQuote = new TopUpQuote();
			topUpQuote.setID(hashMap.get("id").toString());
			topUpQuote.setAmount(new BigDecimal(Integer.parseInt(hashMap.get("amount").toString())));
			topUpQuote.setUsername(hashMap.get("username").toString());
			topUpQuote.setTopUpFee(new BigDecimal(Double.parseDouble(hashMap.get("topUpFee").toString())));
			topUpQuote.setAccessTokenID(hashMap.get("accessTokenID").toString());

			HashMap sourceOfFundMap = (HashMap) hashMap.get("sourceOfFund");
			DirectDebit directDebit = new DirectDebit();
			directDebit.setBankCode(sourceOfFundMap.get("bankCode").toString());
			directDebit.setBankNameEn(sourceOfFundMap.get("bankNameEn").toString());
			directDebit.setBankNameTh(sourceOfFundMap.get("bankNameTh").toString());
			directDebit.setBankAccountNumber(sourceOfFundMap.get("bankAccountNumber").toString());
			directDebit.setMinAmount(new BigDecimal(Integer.parseInt(sourceOfFundMap.get("minAmount").toString())));
			directDebit.setMaxAmount(new BigDecimal(Integer.parseInt(sourceOfFundMap.get("maxAmount").toString())));
			directDebit.setSourceOfFundID(sourceOfFundMap.get("sourceOfFundID").toString());
			directDebit.setSourceOfFundType(sourceOfFundMap.get("sourceOfFundType").toString());
			topUpQuote.setSourceOfFund(directDebit);

			return topUpQuote;
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
			throw new ServiceInventoryException(ServiceInventoryException.Code.GENERAL_ERROR,
					"Can not stored data in repository.");
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
