package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.EwalletFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.EwalletFacade.TopUpBankSystemFailException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.EwalletFacade.TopUpUMarketSystemFailException;

@Service
public class AsyncTopUpEwalletProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpEwalletProcessor.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private EwalletFacade.TopUpBuilder topUpFacade;

	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, AccessToken accessToken) {
		try {

			TopUpQuote quote = topUpOrder.getQuote();

			topUpOrder.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveTopUpEwalletTransaction(topUpOrder, accessToken.getAccessTokenID());

			TopUpConfirmationInfo confirmationInfo = topUpFacade.withAmount(quote.getAmount())
				.usingSourceOfFund(quote.getSourceOfFund())
				.fromUser(accessToken)
				.performTopUp();

			topUpOrder.setConfirmationInfo(confirmationInfo);

			topUpOrder.setStatus(Transaction.Status.SUCCESS);
			logger.info("AsyncService.topUpUtibaEwallet.resultTransactionID: " + confirmationInfo.getTransactionID());

		} catch (TopUpBankSystemFailException e) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.BANK_FAILED);
		} catch (TopUpUMarketSystemFailException e) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.UMARKET_FAILED);
		} catch (Exception ex) {
			topUpOrder.setFailStatus(TopUpOrder.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveTopUpEwalletTransaction(topUpOrder, accessToken.getAccessTokenID());

		return new AsyncResult<TopUpOrder> (topUpOrder);
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	public void setTopUpFacade(EwalletFacade.TopUpBuilder topUpFacade) {
		this.topUpFacade = topUpFacade;
	}

}
