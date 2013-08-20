package th.co.truemoney.serviceinventory.buy.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
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
		
		legacyFacade.buyProduct()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toMobileNumber(accessToken.getMobileNumber())
			.usingSourceOfFund("EW")
			.withAmount(amount)
			.verifyBuyProduct();
		
		BuyProductDraft buyProductDraft = createBuyProductDraft(amount, recipientMobileNumber, accessTokenID);
		transactionRepo.saveDraftTransaction(buyProductDraft, accessToken.getAccessTokenID());
		return buyProductDraft;
	}

	@Override
	public BuyProductDraft getBuyProductDraftDetails(String buyProductDraftID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status performBuyProduct(String buyProductDraftID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getBuyProductStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuyProductTransaction getBuyProductResult(String transactionID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	private BuyProductDraft createBuyProductDraft(BigDecimal amount, String recipientMobileNumber, String accessTokenID) {
		String draftID = UUID.randomUUID().toString();
		BuyProductDraft draft = new BuyProductDraft();
		draft.setID(draftID);
		draft.setAccessTokenID(accessTokenID);
		draft.setAmount(amount);
		draft.setRecipientMobileNumber(recipientMobileNumber);
		draft.setStatus(P2PTransferDraft.Status.CREATED);

		return draft;
	}
	
}
