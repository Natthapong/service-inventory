package th.co.truemoney.serviceinventory.buy;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BuyProductService {

	public BuyProductDraft createAndVerifyBuyProductDraft(String target, String recipientMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyProductDraft getBuyProductDraftDetails(String buyProductDraftID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyProductTransaction.Status performBuyProduct(String buyProductDraftID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyProductTransaction.Status getBuyProductStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException;
	
	public BuyProductTransaction getBuyProductResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException;
	
}
