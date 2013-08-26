package th.co.truemoney.serviceinventory.buy.impl;

import java.math.BigDecimal;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.buy.domain.BuyEpinSms;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductConfirmationInfo;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.SendEpinService;

@Service
public class AsyncBuyProductProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncBuyProductProcessor.class);

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;
	
	@Autowired
	private SendEpinService sendEpinService;
	
	@Async
	public Future<BuyProductTransaction> buyProduct(BuyProductTransaction buyProductTransaction,
			AccessToken accessToken) {
		try {
			BuyProductDraft draftTransaction = buyProductTransaction.getDraftTransaction();
			BuyProduct buyProduct = draftTransaction.getBuyProductInfo();

			BigDecimal amount = buyProduct.getAmount();

			buyProductTransaction.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

			ClientCredential appData = accessToken.getClientCredential();

			String verificationID = draftTransaction.getTransactionID();

			BuyProductConfirmationInfo confirmationInfo = legacyFacade.buyProduct()
				.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
	            .fromChannel(appData.getChannel(), appData.getChannelDetail())
	            .fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
	            .withTargetProduct(draftTransaction.getBuyProductInfo().getTarget())
	            .toRecipientMobileNumber(draftTransaction.getRecipientMobileNumber())
	            .usingSourceOfFund("EW")
	            .withAmount(amount)
	            .andFee(BigDecimal.ZERO, BigDecimal.ZERO)
	            .confirmBuyProduct(verificationID);

			buyProductTransaction.setConfirmationInfo(confirmationInfo);
			buyProductTransaction.setStatus(Transaction.Status.SUCCESS);
			logger.info("AsyncService.buy-product.resultTransactionID: " + confirmationInfo.getTransactionID());
		} catch (ServiceInventoryException e) {
			buyProductTransaction.setStatus(Transaction.Status.FAILED);
			buyProductTransaction.setFailStatus(BuyProductTransaction.FailStatus.UNKNOWN_FAILED);
			buyProductTransaction.setFailCause(e);
		} catch (Exception ex) {
			logger.error("unexpect buy product fail: ", ex);
			buyProductTransaction.setFailStatus(BuyProductTransaction.FailStatus.UNKNOWN_FAILED);
		}		
		transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());
		
		if(buyProductTransaction.getStatus().equals(Transaction.Status.SUCCESS)) {

			BuyEpinSms buyEpinSms = new BuyEpinSms();
			buyEpinSms.setRecipientMobileNumber(buyProductTransaction.getDraftTransaction().getRecipientMobileNumber());
			buyEpinSms.setAmount(buyProductTransaction.getDraftTransaction().getBuyProductInfo().getAmount().toString());
			buyEpinSms.setPin(buyProductTransaction.getConfirmationInfo().getPin());
			buyEpinSms.setSerial(buyProductTransaction.getConfirmationInfo().getSerial());
			buyEpinSms.setTxnID(buyProductTransaction.getID());
			buyEpinSms.setAccount(accessToken.getMobileNumber());
			
			sendEpinService.send(buyEpinSms);
			
		}
		
		return new AsyncResult<BuyProductTransaction> (buyProductTransaction);
	}
}
