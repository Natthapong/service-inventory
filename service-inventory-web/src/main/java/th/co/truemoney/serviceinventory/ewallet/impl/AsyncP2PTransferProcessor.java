package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.Date;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.TransferRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;

@Service
public class AsyncP2PTransferProcessor {

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

	public Future<P2PTransaction> transferEwallet(P2PTransaction p2pTransaction, String accessTokenID, TransferRequest transferRequest) {
		try {
			logger.debug("start time " + new Date());

			p2pTransaction.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveP2PTransaction(p2pTransaction, accessTokenID);

			StandardMoneyResponse standardMoneyResponse = ewalletProxy.transfer(transferRequest);
			logger.debug("finished time " + new Date());

			if (standardMoneyResponse != null) {
				P2PTransactionConfirmationInfo info = new P2PTransactionConfirmationInfo();
				info.setTransactionID(standardMoneyResponse.getTransactionId());
		        Date date = new Date();
		        java.text.SimpleDateFormat df= new java.text.SimpleDateFormat();
		        df.applyPattern("dd/MM/yyyy HH:mm");
				info.setTransactionDate(df.format(date));
				p2pTransaction.setConfirmationInfo(info);
			}

			p2pTransaction.setStatus(Transaction.Status.SUCCESS);
			logger.error("AsyncService.transferEwallet.resultTransactionID: "+standardMoneyResponse.getTransactionId());
		} catch (EwalletException e) {
			logger.error("AsyncService.transferEwallet.resultCode: "+e.getCode());
			logger.error("AsyncService.transferEwallet.resultNamespace: "+e.getNamespace());
			String errorCode = e.getCode();
			if (errorCode.equals("5") || errorCode.equals("6") ||
					errorCode.equals("7") || errorCode.equals("19") ||
					errorCode.equals("27") || errorCode.equals("38")) {
				p2pTransaction.setFailStatus(P2PTransaction.FailStatus.UMARKET_FAILED);
			} else {
				p2pTransaction.setFailStatus(P2PTransaction.FailStatus.UNKNOWN_FAILED);
			}
			logger.error("AsyncService.transferEwallet.resultCode: "+e.getCode());
			logger.error("AsyncService.transferEwallet.resultNamespace: "+e.getNamespace());
		} catch (Exception e) {
			p2pTransaction.setFailStatus(P2PTransaction.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveP2PTransaction(p2pTransaction, accessTokenID);

		return new AsyncResult<P2PTransaction> (p2pTransaction);
		
	}

}
