package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.Date;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.TransferRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;

@Service
public class AsyncTopUpEwalletProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpEwalletProcessor.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	public TransactionRepository getTransactionRepo() {
		return transactionRepo;
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, String accessTokenID, AddMoneyRequest addMoneyRequest) {
		try {


			logger.debug("start time " + new Date());

			topUpOrder.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveTopUpEwalletTransaction(topUpOrder, accessTokenID);

			StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);
			logger.debug("finished time " + new Date());

			if (moneyResponse != null) {
				TopUpConfirmationInfo info = new TopUpConfirmationInfo();
				info.setTransactionID(moneyResponse.getTransactionId());
		        Date date = new Date();
		        java.text.SimpleDateFormat df= new java.text.SimpleDateFormat();
		        df.applyPattern("dd/MM/yyyy HH:mm");
				info.setTransactionDate(df.format(date));
				topUpOrder.setConfirmationInfo(info);
			}

			topUpOrder.setStatus(Transaction.Status.SUCCESS);
			logger.error("AsyncService.topUpUtibaEwallet.resultTransactionID: "+moneyResponse.getTransactionId());
		} catch (EwalletException e) {
			logger.error("AsyncService.topUpUtibaEwallet.resultCode: "+e.getCode());
			logger.error("AsyncService.topUpUtibaEwallet.resultNamespace: "+e.getNamespace());
			String errorCode = e.getCode();
			if (errorCode.equals("24003") || errorCode.equals("24008") || errorCode.equals("24010") || errorCode.equals("25007")) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.BANK_FAILED);
			} else if (errorCode.equals("5") || errorCode.equals("6") ||
					errorCode.equals("7") || errorCode.equals("19") ||
					errorCode.equals("27") || errorCode.equals("38")) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.UMARKET_FAILED);
			} else {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.UNKNOWN_FAILED);
			}
			logger.error("AsyncService.topUpUtibaEwallet.resultCode: "+e.getCode());
			logger.error("AsyncService.topUpUtibaEwallet.resultNamespace: "+e.getNamespace());
		} catch (Exception e) {
			topUpOrder.setFailStatus(TopUpOrder.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveTopUpEwalletTransaction(topUpOrder, accessTokenID);

		return new AsyncResult<TopUpOrder> (topUpOrder);
	}
	
	@Async
	public Future<P2PTransaction> transferEwallet(P2PTransaction p2pTransaction, String accessTokenID, TransferRequest transferRequest) {
		return new AsyncResult<P2PTransaction> (p2pTransaction);
	}

}
