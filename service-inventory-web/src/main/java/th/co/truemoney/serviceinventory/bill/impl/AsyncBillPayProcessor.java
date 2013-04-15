package th.co.truemoney.serviceinventory.bill.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
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

	public Future<BillPaymentTransaction> payBill(BillPaymentTransaction billPaymentReceipt, AccessToken accessToken) {

		try {
			BillPaymentDraft draftTransaction = billPaymentReceipt.getDraftTransaction();
			Bill billInfo = draftTransaction.getBillInfo();
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

			BillPaymentConfirmationInfo confirmationInfo = legacyFacade.billing()
					.fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
					.aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
					.withMsisdn(accessToken.getMobileNumber())
					.fromApp("MOBILE_IPHONE", "IPHONE+1", "f7cb0d495ea6d989")
					.fromBillChannel("iPhone", "iPhone Application")
					.paying(billInfo.getAmount(), billInfo.getServiceFee(), billInfo.getSourceOfFundFees()[0])
					.performPayment();

			billPaymentReceipt.setConfirmationInfo(confirmationInfo);
			billPaymentReceipt.setStatus(Transaction.Status.SUCCESS);

			logger.info("AsyncService.payBill.resultTransactionID: " + confirmationInfo.getTransactionID());
		} catch (UMarketSystemTransactionFailException e) {
			billPaymentReceipt.setFailStatus(BillPaymentTransaction.FailStatus.UMARKET_FAILED);
			// TODO : Add more exception
		} catch (Exception ex) {
			logger.error("unexpect bill payment fail: ", ex);
			billPaymentReceipt.setFailStatus(BillPaymentTransaction.FailStatus.UNKNOWN_FAILED);
		}

		transactionRepo.saveBillPayment(billPaymentReceipt, accessToken.getAccessTokenID());

		return new AsyncResult<BillPaymentTransaction> (billPaymentReceipt);
	}

}
