package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.BillPaymentDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BillpayService {
	
	public BillPaymentInfo getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException;
			
	public BillPaymentDraftTransaction createDraftTransaction(BillPaymentInfo billpayInfo, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentDraftTransaction getDraftTransactionDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP sendOTP(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public DraftTransaction.Status confirmDraftTransaction(String draftTransactionID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public Transaction.Status getTransactionStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction getTransactionResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;
	
}
