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
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpMobileFacade.SIEngineTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpMobileFacade.UMarketSystemTransactionFailException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Service
public class AsyncTopUpMobileProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpMobileProcessor.class);
	
	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;

	@Async
	public Future<TopUpMobileTransaction> topUpMobile(TopUpMobileTransaction topUpMobileTransaction, AccessToken accessToken) {
		try {
			TopUpMobileDraft topUpMobileDraft = topUpMobileTransaction.getDraftTransaction();
			TopUpMobile topUpMobile = topUpMobileDraft.getTopUpMobileInfo();
			
			BigDecimal amount = topUpMobile.getAmount();

			topUpMobileTransaction.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveTopUpMobileTransaction(topUpMobileTransaction, accessToken.getAccessTokenID());

			String verificationID = topUpMobileDraft.getTransactionID();
			String target = topUpMobile.getTarget();
			
			TopUpMobileConfirmationInfo confirmationInfo = legacyFacade.topUpMobile()
					.topUpAirtime(topUpMobile.getMobileNumber(), 
								  amount,
								  topUpMobile.getServiceFee().calculateFee(amount), 
								  topUpMobile.getEwalletSourceOfFund().calculateFee(amount),
								  verificationID, 
								  target,
								  accessToken);

			topUpMobileTransaction.setConfirmationInfo(confirmationInfo);
			topUpMobileTransaction.setStatus(Transaction.Status.SUCCESS);

			logger.info("AsyncService.topUpMobile.resultTransactionID: " + confirmationInfo.getTransactionID());
		} catch (UMarketSystemTransactionFailException ex) {
			topUpMobileTransaction.setFailStatus(TopUpMobileTransaction.FailStatus.UMARKET_FAILED);
		} catch (SIEngineTransactionFailException ex) {
			topUpMobileTransaction.setFailStatus(TopUpMobileTransaction.FailStatus.TPP_FAILED);
		} catch (Exception ex) {
			topUpMobileTransaction.setFailStatus(TopUpMobileTransaction.FailStatus.UNKNOWN_FAILED);
		}
		return new AsyncResult<TopUpMobileTransaction>(topUpMobileTransaction);
	}
	
}
