package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.ServiceUnavailableException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class P2PTransferServiceImpl implements P2PTransferService {

	@Autowired
	private AccessTokenRepository accessTokenRepo;
	
	@Autowired
	private EwalletSoapProxy ewalletProxy;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Override
	public P2PDraftTransaction createDraftTransaction(
			P2PDraftRequest p2pDraftRequest, String accessTokenID) {
		
		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = getAccessTokenByID(accessTokenID);
		
		//--- Send to verify amount ---//
		StandardMoneyResponse verifyResponse;
		try {
			verifyResponse = VerifyEwalletTransfer(p2pDraftRequest, accessToken);
		} catch (EwalletException e) {
			throw new ServiceInventoryException(e.getCode(), "verify tranfer fail.", e.getNamespace());
		} catch (ServiceUnavailableException e) {
			throw new ServiceInventoryException(
					Integer.toString(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
					e.getMessage(), e.getNamespace());
		}
		
		//--- Generate Response ---//
		String fullName = getFullNameFromStandardMoneyResponse(verifyResponse);
		String markFullName = markFullName(fullName);
		
		String draftID = UUID.randomUUID().toString();
		P2PDraftTransaction draft = new P2PDraftTransaction();
		draft.setID(draftID);
		draft.setAccessTokenID(accessTokenID);
		draft.setAmount(p2pDraftRequest.getAmount());
		draft.setMobileNumber(p2pDraftRequest.getMobileNumber());
		draft.setFullname(markFullName);
		
		transactionRepo.saveP2PDraftTransaction(draft);
		
		return draft;
	}

	private StandardMoneyResponse VerifyEwalletTransfer(P2PDraftRequest p2pDraftRequest,
			AccessToken accessToken) {
		VerifyTransferRequest verifyRequest = new VerifyTransferRequest();
		verifyRequest.setChannelId(accessToken.getChannelID());
		verifyRequest.setAmount(p2pDraftRequest.getAmount());
		verifyRequest.setTarget(p2pDraftRequest.getMobileNumber());
		
		StandardMoneyResponse verifyResponse = ewalletProxy.verifyTransfer(verifyRequest);
		
		return verifyResponse;
	}
	
	private String getFullNameFromStandardMoneyResponse(StandardMoneyResponse resp) {
		String fullName = "";
		
		for (int i=0; i<resp.getDetailKey().length; ++i) {
			if ("fullName".equals(resp.getDetailKey()[i])) {
				fullName = resp.getDetailValue()[i];
				break;
			}
		}
		
		return fullName;
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
	public P2PTransaction createTransaction(String draftTransactionID, OTP otp,
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

	private AccessToken getAccessTokenByID(String accessTokenID) {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		if (accessToken == null) {
			throw new ServiceInventoryException(
					ServiceInventoryException.Code.ACCESS_TOKEN_NOT_FOUND,
					"AccessTokenID is expired or not found.");
		}

		return accessToken;
	}
	
	private String markFullName(String fullName)
	{
		fullName = fullName.trim();
		String markName;
		
		if (fullName == null || "".equals(fullName)) {
			markName = "-";
		} else if (fullName.contains(" ")) {
			String[] name = fullName.split("\\s{1,}"); // split space 1 or more
			String markLastName = "";
			
			if (name[1].length() > 3) {
				markLastName = String.format("%s***", name[1].substring(0, 3));	
			} else {
				markLastName = String.format("%s***", name[1].substring(0, 1));
			}
			
			markName = String.format("%s %s", name[0], markLastName);
		} else {
			if (fullName.length() > 5) {
				markName = String.format("%s***", fullName.substring(0, 5));
			} else {
				markName = fullName;
			}
		}
		
		return markName;
	}
	
	public EwalletSoapProxy getEwalletProxy() {
		return ewalletProxy;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public AccessTokenRepository getAccessTokenRepository() {
		return accessTokenRepo;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public TransactionRepository getTransactionRepository() {
		return transactionRepo;
	}

	public void setTransactionRepository(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}
	
	
}
