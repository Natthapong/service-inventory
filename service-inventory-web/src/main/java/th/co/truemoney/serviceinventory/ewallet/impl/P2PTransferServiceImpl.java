package th.co.truemoney.serviceinventory.ewallet.impl;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;

public class P2PTransferServiceImpl implements P2PTransferService {

	@Override
	public P2PDraftTransaction createDraftTransaction(
			P2PDraftRequest p2pDraftRequest, String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PDraftTransaction getDraftTransactionDetail(
			String draftTransactionID, String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PDraftTransaction sendOTP(String draftTransactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PTransaction createTransaction(String draftTransactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PTransactionStatus getTransactionStatus(String transactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PTransaction getTransactionDetail(String transactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

}
