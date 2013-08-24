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
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction.FailStatus;

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
			transactionRepo.saveTransaction(topUpMobileTransaction, accessToken.getAccessTokenID());

			String verificationID = topUpMobileDraft.getTransactionID();
			String target = topUpMobile.getTarget();

			ClientCredential appData = accessToken.getClientCredential();

			TopUpMobileConfirmationInfo confirmationInfo = legacyFacade.topUpMobile()
					.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
					.fromTopUpChannel(appData.getChannel(), appData.getChannelDetail())
					.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
					.toMobileNumber(topUpMobile.getMobileNumber())
					.usingSourceOfFund("EW")
					.withAmount(amount)
					.andFee(topUpMobile.getServiceFee().calculateFee(amount), topUpMobile.getEwalletSourceOfFund().calculateFee(amount))
					.topUpAirtime(verificationID, target);

			topUpMobileTransaction.setConfirmationInfo(confirmationInfo);
			topUpMobileTransaction.setStatus(Transaction.Status.SUCCESS);

			logger.info("AsyncService.topUpMobile.resultTransactionID: " + confirmationInfo.getTransactionID());
		} catch (ServiceInventoryException e) {
			topUpMobileTransaction.setStatus(Transaction.Status.FAILED);
			topUpMobileTransaction.setFailStatus(FailStatus.UNKNOWN_FAILED);
			topUpMobileTransaction.setFailCause(e);
		} catch (Exception ex) {
			logger.error("unexpect top up fail: ", ex);
			topUpMobileTransaction.setFailStatus(FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveTransaction(topUpMobileTransaction, accessToken.getAccessTokenID());

		return new AsyncResult<TopUpMobileTransaction>(topUpMobileTransaction);
	}

}
