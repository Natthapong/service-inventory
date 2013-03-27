package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;

public interface P2PTransferService {
	
	public P2PDraftTransaction createDraftTransaction(P2PDraftRequest p2pDraftRequest, String accessTokenID);

	public P2PDraftTransaction getDraftTransactionDetail(String draftTransactionID, String accessTokenID);
	
	public P2PDraftTransaction sendOTP(String draftTransactionID, String accessTokenID);
	
	public P2PTransaction createTransaction(String draftTransactionID, OTP otp, String accessTokenID);
	
	public P2PTransactionStatus getTransactionStatus(String transactionID, String accessTokenID);
	
	public P2PTransaction getTransactionDetail(String transactionID, String accessTokenID);
	
}
