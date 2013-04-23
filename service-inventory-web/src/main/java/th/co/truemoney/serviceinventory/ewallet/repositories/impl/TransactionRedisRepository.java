package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.InternalServerErrorException;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionRedisRepository implements TransactionRepository {

	private static Logger logger = LoggerFactory.getLogger(TransactionRedisRepository.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private RedisLoggingDao redisLoggingDao;

	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote, String accessTokenID) throws ServiceInventoryWebException {
		try {
			redisLoggingDao.addData("quote:" + accessTokenID + ":" + topupQuote.getID(), mapper.writeValueAsString(topupQuote), 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public TopUpQuote findTopUpQuote(String draftID, String accessTokenID) throws ServiceInventoryWebException {
		try {
			String key = "quote:" + accessTokenID + ":" + draftID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "qoute not found: " + key);
			}
			return mapper.readValue(result, TopUpQuote.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveTopUpOrder(TopUpOrder topupOrder, String accessTokenID) throws ServiceInventoryWebException {
		try {
			redisLoggingDao.addData("order:" + accessTokenID + ":" + topupOrder.getID(), mapper.writeValueAsString(topupOrder), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public TopUpOrder findTopUpOrder(String orderID, String accessTokenID) throws ServiceInventoryWebException {
		try {
			String key = "order:" + accessTokenID + ":" + orderID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "TopUp Ewallet order not found: " + key);
			}

			return mapper.readValue(result, TopUpOrder.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveP2PTransferDraft(P2PTransferDraft p2pTransferDraft, String accessTokenID) {
		try {
			redisLoggingDao.addData("p2pdraft:" + accessTokenID + ":" +p2pTransferDraft.getID(), mapper.writeValueAsString(p2pTransferDraft), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not stored data in repository.", e);
		}
	}

	@Override
	public P2PTransferDraft findP2PTransferDraft(String p2pTransferDraftID, String accessTokenID) {
		try {
			String key = "p2pdraft:" + accessTokenID + ":" +p2pTransferDraftID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.DRAFT_TRANSACTION_NOT_FOUND, "P2P transfer draft not found: " + key);
			}

			return mapper.readValue(result, P2PTransferDraft.class);
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveP2PTransferTransaction(P2PTransferTransaction p2pTransaction, String accessTokenID) {
		try {
			redisLoggingDao.addData("p2pTrans:" + accessTokenID + ":" +p2pTransaction.getID(), mapper.writeValueAsString(p2pTransaction), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public P2PTransferTransaction findP2PTransferTransaction(String p2pTransactionID, String accessTokenID) {
		try {
			String key = "p2pTrans:" + accessTokenID + ":" + p2pTransactionID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "P2P transfer transaction not found: " + key);
			}

			return mapper.readValue(result, P2PTransferTransaction.class);
		} catch (ServiceInventoryWebException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override

	public void saveBillPaymentDraft(
			BillPaymentDraft billInvoice,
			String accessTokenID) {
		try {
			redisLoggingDao.addData("billInvoice:" + accessTokenID + ":" +billInvoice.getID(), mapper.writeValueAsString(billInvoice), 15L);
		} catch (Exception e) {
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public BillPaymentDraft findBillPaymentDraft(String billInvoiceID, String accessTokenID) {
		try {
			String key = "billInvoice:" + accessTokenID + ":" + billInvoiceID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.DRAFT_TRANSACTION_NOT_FOUND, "Bill invoice not found: " + key);
			}

			return mapper.readValue(result, BillPaymentDraft.class);
		} catch (ServiceInventoryWebException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveBillPaymentTransaction(BillPaymentTransaction billPayment, String accessTokenID) {
		try {
			redisLoggingDao.addData("billPayment:" + accessTokenID + ":" + billPayment.getID(), mapper.writeValueAsString(billPayment), 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public BillPaymentTransaction findBillPaymentTransaction(String billPaymentID, String accessTokenID) {

		try {
			String key = "billPayment:" + accessTokenID + ":" + billPaymentID;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "Bill payment not found: " + key);
			}

			return mapper.readValue(result, BillPaymentTransaction.class);
		} catch (ServiceInventoryWebException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

	@Override
	public void saveTopUpMobileDraft(TopUpMobileDraft topUpMobileDraft,
			String accessTokenID) {
		try {
			redisLoggingDao.addData("topUpMobile:" + accessTokenID + ":" + topUpMobileDraft.getID(), mapper.writeValueAsString(topUpMobileDraft), 15L);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not store data in repository.", e);
		}
	}

	@Override
	public TopUpMobileDraft findTopUpMobileDraft(String topUpMobileId,
			String accessTokenID) {
		try {
			String key = "topUpMobile:" + accessTokenID + ":" + topUpMobileId;
			String result = redisLoggingDao.getData(key);
			if(result == null) {
				throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND, "Topup Mobile not found: " + key);
			}

			return mapper.readValue(result, TopUpMobileDraft.class);
		} catch (ServiceInventoryWebException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new InternalServerErrorException(Code.GENERAL_ERROR, "Can not read data in repository.", e);
		}
	}

}
