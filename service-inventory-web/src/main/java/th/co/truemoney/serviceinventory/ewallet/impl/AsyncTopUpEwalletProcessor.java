package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.legacyfacade.facade.BalanceFacade.BankSystemTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.BalanceFacade.UMarketSystemTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.LegacyFacade;

@Service
public class AsyncTopUpEwalletProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpEwalletProcessor.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;

	@Async
	public Future<TopUpOrder> topUpUtibaEwallet(TopUpOrder topUpOrder, AccessToken accessToken) {
		try {

			TopUpQuote quote = topUpOrder.getQuote();
			BigDecimal amount = quote.getAmount();
			SourceOfFund sourceOfFund = quote.getSourceOfFund();

			topUpOrder.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveTransaction(topUpOrder, accessToken.getAccessTokenID());

			TopUpConfirmationInfo confirmationInfo =
					legacyFacade.fromChannel(accessToken.getChannelID())
					.topUp(amount)
					.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
					.usingSourceOFFund(sourceOfFund.getSourceOfFundID(), sourceOfFund.getSourceOfFundType())
					.performTopUp();

			topUpOrder.setConfirmationInfo(confirmationInfo);
			topUpOrder.setStatus(Transaction.Status.SUCCESS);

			logger.info("AsyncService.topUpUtibaEwallet.resultTransactionID: " + confirmationInfo.getTransactionID());

		} catch (BankSystemTransactionFailException e) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.BANK_FAILED);
		} catch (UMarketSystemTransactionFailException e) {
				topUpOrder.setFailStatus(TopUpOrder.FailStatus.UMARKET_FAILED);
		} catch (Exception ex) {
			logger.error("unexpect top up fail: ", ex);
			topUpOrder.setFailStatus(TopUpOrder.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveTransaction(topUpOrder, accessToken.getAccessTokenID());

		return new AsyncResult<TopUpOrder> (topUpOrder);
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}

}
