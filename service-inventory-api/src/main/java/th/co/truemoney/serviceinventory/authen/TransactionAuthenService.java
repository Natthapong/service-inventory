package th.co.truemoney.serviceinventory.authen;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TransactionAuthenService {

	public OTP requestOTP(String draftID, String accessTokenID) throws ServiceInventoryException;
	public DraftTransaction.Status verifyOTP(String draftID, OTP otp, String accessTokenID) throws ServiceInventoryException;
}
