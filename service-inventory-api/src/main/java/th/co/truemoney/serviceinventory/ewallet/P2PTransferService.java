package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface P2PTransferService {

	public P2PDraftTransaction createDraftTransaction(String toMobileNo, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;

	public P2PDraftTransaction getDraftTransactionDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP sendOTP(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransactionStatus createTransaction(String draftTransactionID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransactionStatus getTransactionStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransaction getTransactionResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

}
