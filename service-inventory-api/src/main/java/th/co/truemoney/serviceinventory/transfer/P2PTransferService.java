package th.co.truemoney.serviceinventory.transfer;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

public interface P2PTransferService {

	public P2PTransferDraft createAndVerifyTransferDraft(String toMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransferDraft getTransferDraftDetails(String transferDraftID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP requestOTP(String transferDraftID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransferDraft.Status verifyOTP(String transferDraftID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransferTransaction.Status getTransferringStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

	public P2PTransferTransaction getTransactionResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

}
