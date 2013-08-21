package th.co.truemoney.serviceinventory.buy.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

public class BuyProductServiceImpl implements BuyProductService {
	
	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;
	
	@Override
	public BuyProductDraft createAndVerifyBuyProductDraft(String target,
			String recipientMobileNumber, BigDecimal amount,
			String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = accessToken.getClientCredential();
		
		if (recipientMobileNumber == null || recipientMobileNumber.trim().isEmpty()) {
			recipientMobileNumber = accessToken.getMobileNumber();
		}
		
		BuyProduct buyProduct = legacyFacade.buyProduct()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.withTargetProduct(target)
			.toRecipientMobileNumber(recipientMobileNumber)
			.usingSourceOfFund("EW")
			.withAmount(amount)
			.verifyBuyProduct();
		
		BuyProductDraft buyProductDraft = createBuyProductDraft(target, recipientMobileNumber, accessTokenID, buyProduct);
		transactionRepo.saveDraftTransaction(buyProductDraft, accessToken.getAccessTokenID());
		return buyProductDraft;
	}

	@Override
	public BuyProductDraft getBuyProductDraftDetails(String buyProductDraftID, 
			String accessTokenID) throws ServiceInventoryException {
		return transactionRepo.findDraftTransaction(buyProductDraftID, accessTokenID, BuyProductDraft.class);
	}

	@Override
	public Status performBuyProduct(String buyProductDraftID,
			String accessTokenID) throws ServiceInventoryException {
		return null;
	}

	@Override
	public Status getBuyProductStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		BuyProductTransaction buyProduct = getBuyProductResult(transactionID, accessTokenID);
	  if (Transaction.Status.FAILED == buyProduct.getStatus()) {
            ServiceInventoryException failCause = buyProduct.getFailCause();
            if (failCause != null) {
            	throw new ServiceInventoryException(
            			HttpStatus.BAD_REQUEST.value(), 
            			failCause.getErrorCode(), 
            			failCause.getDeveloperMessage(), 
            			failCause.getErrorNamespace());
            }
            throw new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail.");
        }
        return buyProduct.getStatus();
	}

	@Override
	public BuyProductTransaction getBuyProductResult(String transactionID, String accessTokenID) throws ServiceInventoryException {
        return transactionRepo.findTransaction(transactionID, accessTokenID, BuyProductTransaction.class);
	}

	public void setAccessTokenRepo(AccessTokenMemoryRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setTransactionRepo(TransactionRepositoryImpl transactionRepo) {
		this.transactionRepo = transactionRepo;
	}
	
	private BuyProductDraft createBuyProductDraft(String target, String recipientMobileNumber, String accessTokenID, BuyProduct buyProduct) {
		String draftID = UUID.randomUUID().toString();
		BuyProductDraft buyProductDraft = new BuyProductDraft(draftID, buyProduct, buyProduct.getAmount(), buyProduct.getID(), target, recipientMobileNumber);
		buyProductDraft.setAccessTokenID(accessTokenID);
		buyProductDraft.setStatus(P2PTransferDraft.Status.CREATED);

		return buyProductDraft;
	}
	
}
