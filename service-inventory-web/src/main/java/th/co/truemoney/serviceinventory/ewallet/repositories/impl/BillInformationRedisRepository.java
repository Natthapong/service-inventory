package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BillInformationRedisRepository implements BillInformationRepository {

	private static Logger logger = LoggerFactory.getLogger(BillInformationRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private ExpirableMap redisLoggingDao;

	@Override
	public Bill findBill(String billID, String accessTokenID) {

		try {
			String result = redisLoggingDao.getData("bill:" + accessTokenID + ":" + billID);
			if(result == null) {
				throw new ResourceNotFoundException(Code.BILL_NOT_FOUND, "bill not found.");
			}

			return mapper.readValue(result, Bill.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public void saveBill(Bill bill, String accessTokenID) {
		if (bill != null) {
			try {
				redisLoggingDao.addData("bill:" + accessTokenID + ":" + bill.getID(), mapper.writeValueAsString(bill), 20L);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
			}
		}
	}

}
