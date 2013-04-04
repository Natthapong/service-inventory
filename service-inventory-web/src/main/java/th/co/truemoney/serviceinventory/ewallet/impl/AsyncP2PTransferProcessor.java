package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade.UMarketSystemTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;

@Service
public class AsyncP2PTransferProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpEwalletProcessor.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;

	public Future<P2PTransaction> transferEwallet(P2PTransaction p2pTransaction, AccessToken accessToken) {
		try {

			P2PDraftTransaction p2pDraftTransaction = p2pTransaction.getDraftTransaction();

			Integer channelID = accessToken.getChannelID();
			String sessionID = accessToken.getSessionID();
			String truemoneyID = accessToken.getTruemoneyID();

			String sourceMobileNumber = accessToken.getMobileNumber();
			String targetMobileNumber = p2pDraftTransaction.getMobileNumber();

			BigDecimal amount = p2pDraftTransaction.getAmount();

			if (sourceMobileNumber != null && sourceMobileNumber.equals(targetMobileNumber)) {
				throw new ServiceInventoryWebException(400, Code.INVALID_TARGET_MOBILE_NUMBER, "Invalid target mobile number");
			}

			p2pTransaction.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveP2PTransaction(p2pTransaction, accessToken.getAccessTokenID());

			P2PTransactionConfirmationInfo confirmationInfo = legacyFacade
					.fromChannel(channelID)
					.transfer(amount)
					.fromUser(sessionID, truemoneyID)
					.toTargetUser(targetMobileNumber)
					.performTransfer();

			p2pTransaction.setConfirmationInfo(confirmationInfo);

			p2pTransaction.setStatus(Transaction.Status.SUCCESS);
			logger.info("AsyncService.transferEwallet.resultTransactionID: " + confirmationInfo.getTransactionID());

		} catch (UMarketSystemTransactionFailException e) {
			p2pTransaction.setFailStatus(FailStatus.UMARKET_FAILED);
		} catch (Exception ex) {
			logger.error("unexpect p2p transfer fail: ", ex);
			p2pTransaction.setFailStatus(FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveP2PTransaction(p2pTransaction, accessToken.getAccessTokenID());

		return new AsyncResult<P2PTransaction> (p2pTransaction);
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}

}
