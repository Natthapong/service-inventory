package th.co.truemoney.serviceinventory.buy;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.buy.domain.BuyEPINDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyEPINTransaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BuyEPINService {

	public BuyEPINDraft createAndVerifyBuyEPINDraft(String toMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyEPINDraft getBuyEPINDraftDetails(String buyEPINDraftID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyEPINTransaction.Status performBuyEPIN(String buyEPINDraftID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyEPINTransaction.Status getBuyEPINStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyEPINTransaction getBuyEPINResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;
	
}
