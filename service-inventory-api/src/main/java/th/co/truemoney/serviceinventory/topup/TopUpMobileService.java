package th.co.truemoney.serviceinventory.topup;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

public interface TopUpMobileService {

	public TopUpMobileDraft verifyAndCreateTopUpMobileDraft(String targetMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;

	public TopUpMobileDraft getTopUpMobileDraftDetail(String draftID, String accessTokenID)
			throws ServiceInventoryException;

	public TopUpMobileTransaction.Status performTopUpMobile(String draftID, String accessTokenID)
			throws ServiceInventoryException;

	public TopUpMobileTransaction.Status getTopUpMobileStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

	public TopUpMobileTransaction getTopUpMobileResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;

}
