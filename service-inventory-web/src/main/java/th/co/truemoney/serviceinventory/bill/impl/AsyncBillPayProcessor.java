package th.co.truemoney.serviceinventory.bill.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncTopUpEwalletProcessor;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade.UMarketSystemTransactionFailException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;

@Service
public class AsyncBillPayProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncTopUpEwalletProcessor.class);
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private LegacyFacade legacyFacade;
	
	public Future<BillPayment> payBill(BillPayment billPaymentReceipt, AccessToken accessToken) {
		
		try {
			Bill draftTransaction = billPaymentReceipt.getDraftTransaction();
			BillInfo billPaymentInfo = draftTransaction.getBillInfo();
			/*
			BigDecimal amount = billPaymentInfo.getAmount();
			SourceOfFundFee sourceOfFundFees[] = billPaymentInfo.getSourceOfFundFees();
			SourceOfFundFee sourceOfFundFee = null;
			int i = 0;
			*/
			billPaymentReceipt.setStatus(Transaction.Status.PROCESSING);
			transactionRepo.saveBillPayment(billPaymentReceipt, accessToken.getAccessTokenID());

/*
			for (i = 0; i < sourceOfFundFees.length; i++) {
				if (sourceOfFundFees[i].getSourceType().equals("EW")) {
					sourceOfFundFee = sourceOfFundFees[i]; 
				}
			}
	*/		
			BillPaymentConfirmationInfo confirmationInfo = legacyFacade
					.fromChannel(accessToken.getChannelID())
					.payBill(billPaymentInfo)
					.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID(), accessToken.getMobileNumber())
					/*
					.usingSourceOfFundFee(sourceOfFundFee)
					.usingTransaction(draftTransaction.getID())
					.forService(billPaymentInfo.getRef1(), billPaymentInfo.getRef2())
					*/
					.performBillPayment();
			
			billPaymentReceipt.setConfirmationInfo(confirmationInfo);
			billPaymentReceipt.setStatus(Transaction.Status.SUCCESS);
			
			logger.info("AsyncService.payBill.resultTransactionID: " + confirmationInfo.getTransactionID());
		} catch (UMarketSystemTransactionFailException e) {
			billPaymentReceipt.setFailStatus(BillPayment.FailStatus.UMARKET_FAILED);
			// TODO : Add more exception
		} catch (Exception ex) {
			logger.error("unexpect bill payment fail: ", ex);
			billPaymentReceipt.setFailStatus(BillPayment.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveBillPayment(billPaymentReceipt, accessToken.getAccessTokenID());
		
		return new AsyncResult<BillPayment> (billPaymentReceipt);
	}

}
