package th.co.truemoney.serviceinventory.buy.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.buy.BuyEPINService;
import th.co.truemoney.serviceinventory.buy.domain.BuyEPINDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyEPINTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

public class BuyEPINServiceImpl implements BuyEPINService {
	
	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;
	
	@Override
	public BuyEPINDraft createAndVerifyBuyEPINDraft(String toMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		ClientCredential appData = accessToken.getClientCredential();
		
		legacyFacade.buyProduct()
			.fromApp(appData.getAppUser(), appData.getAppPassword(), appData.getAppKey())
			.fromChannel(appData.getChannel(), appData.getChannelDetail())
			.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.toMobileNumber(toMobileNumber)
			.usingSourceOfFund("EW")
			.withAmount(amount)
			.verifyBuyProduct();
		
		BuyEPINDraft buyEPINDraft = createDraft(amount, toMobileNumber, accessTokenID);
		transactionRepo.saveDraftTransaction(buyEPINDraft, accessToken.getAccessTokenID());
		return buyEPINDraft;
	}

	@Override
	public BuyEPINDraft getBuyEPINDraftDetails(String buyEPINDraftID, String accessTokenID) throws ServiceInventoryException {
		return null;
	}

	@Override
	public Status performBuyEPIN(String buyEPINDraftID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getBuyEPINStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuyEPINTransaction getBuyEPINResult(String transactionID, String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	private BuyEPINDraft createDraft(BigDecimal amount, String targetMobileNumber, String byAccessToken) {
		String draftID = UUID.randomUUID().toString();
		BuyEPINDraft draft = new BuyEPINDraft();
		draft.setID(draftID);
		draft.setAccessTokenID(byAccessToken);
		draft.setAmount(amount);
		draft.setMobileNumber(targetMobileNumber);
		draft.setStatus(P2PTransferDraft.Status.CREATED);

		return draft;
	}
	
}
