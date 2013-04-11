package th.co.truemoney.serviceinventory.transfer;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

public interface P2PTransferService {

	public P2PDraftTransaction verifyAndCreateTransferDraft(String toMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;

	public P2PDraftTransaction getTransferDraftDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP submitTransferral(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PDraftTransaction.Status verifyOTPAndPerformTransferring(String draftTransactionID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public Transaction.Status getTransferingStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransaction getTransactionResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

}
