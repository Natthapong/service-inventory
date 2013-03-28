package th.co.truemoney.serviceinventory.ewallet.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransactionStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class P2PTransferServiceImpl implements P2PTransferService {

	private static final Logger logger = LoggerFactory.getLogger(P2PTransferServiceImpl.class);

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private AsyncService asyncService;

	@Autowired
	private OTPService otpService;

	@Override
	public P2PDraftTransaction createDraftTransaction(
			P2PDraftRequest p2pDraftRequest, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PDraftTransaction getDraftTransactionDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException {		
		AccessToken accessToken = getAccessTokenByID(accessTokenID);

		P2PDraftTransaction p2pDraftTransaction = transactionRepo.getP2PDraftTransaction(draftTransactionID);

		if (p2pDraftTransaction == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.DRAFT_TRANSACTION_NOT_FOUND, "draft transaction not found");
		}

		if (!accessToken.getAccessTokenID().equals(p2pDraftTransaction.getAccessTokenID())) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.DRAFT_TRANSACTION_NOT_FOUND, "draft transaction not found");
		}

		return p2pDraftTransaction;
	}

	@Override
	public OTP sendOTP(String draftTransactionID, String accessTokenID) 
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		P2PDraftTransaction p2pDraftTransaction = getDraftTransactionDetails(draftTransactionID, accessTokenID);
		p2pDraftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		p2pDraftTransaction.setStatus(P2PDraftTransactionStatus.OTP_SENT);

		transactionRepo.saveP2PDraftTransaction(p2pDraftTransaction);
		
		return otp;
	}

	@Override
	public P2PTransactionStatus createTransaction(String draftTransactionID, OTP otp,	String accessTokenID) 
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		P2PDraftTransaction p2pDraftTransaction = getDraftTransactionDetails(draftTransactionID, accessTokenID);
		
		if(!otpService.isValidOTP(otp)){
			throw new ServiceInventoryException( ServiceInventoryException.Code.OTP_NOT_MATCH, "Invalide OTP.");
		}

		p2pDraftTransaction.setStatus(P2PDraftTransactionStatus.OTP_CONFIRMED);
		transactionRepo.saveP2PDraftTransaction(p2pDraftTransaction);

		P2PTransaction p2pTransaction = new P2PTransaction(p2pDraftTransaction);
		p2pTransaction.setStatus(P2PTransactionStatus.ORDER_VERIFIED);
		transactionRepo.saveP2PTransaction(p2pTransaction);

		performTransferMoney(accessToken, p2pTransaction);

		return p2pTransaction.getStatus();
	}

	@Override
	public P2PTransactionStatus getTransactionStatus(String transactionID, String accessTokenID) 
			throws ServiceInventoryException {
		P2PTransactionStatus p2pTransactionStatus = getTransactionResult(transactionID, accessTokenID).getStatus();

		if (p2pTransactionStatus == P2PTransactionStatus.UMARKET_FAILED) {
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_UMARKET_FAILED,
					"u-market confirmation processing fail.");
		} else if (p2pTransactionStatus == P2PTransactionStatus.FAILED){
			throw new ServiceInventoryException( ServiceInventoryException.Code.CONFIRM_FAILED,
					"confirmation processing fail.");
		}

		return p2pTransactionStatus;
	}

	@Override
	public P2PTransaction getTransactionResult(String transactionID, String accessTokenID) 
			throws ServiceInventoryException {
		AccessToken accessToken = getAccessTokenByID(accessTokenID);

		P2PTransaction p2pTransaction = transactionRepo.getP2PTransaction(transactionID);

		if (p2pTransaction == null || !p2pTransaction.getDraftTransaction().getAccessTokenID().equals(accessToken.getAccessTokenID())) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.TRANSACTION_NOT_FOUND, "transaction not found");
		}

		return p2pTransaction;
	}
	
	private AccessToken getAccessTokenByID(String accessTokenID) {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		return accessToken;
	}
	
	private void performTransferMoney(AccessToken accessToken, P2PTransaction p2pTransaction) {
		
	}

}
