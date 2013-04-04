package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction.FailStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class P2PTransferServiceImpl implements P2PTransferService {
	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private AsyncP2PTransferProcessor asyncP2PTransferProcessor;

	@Autowired
	private OTPService otpService;

	@Override
	public P2PDraftTransaction createDraftTransaction(String toMobileNumber, BigDecimal amount, String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		//--- Send to verify amount ---//
		VerifyTransferResponse verifyResponse = verifyEwalletTransfer(toMobileNumber, amount, accessToken);

		//--- Generate Response ---//
		String fullName = verifyResponse.getTargetFullname();
		String markFullName = markFullName(fullName);

		String draftID = UUID.randomUUID().toString();
		P2PDraftTransaction draft = new P2PDraftTransaction();
		draft.setID(draftID);
		draft.setAccessTokenID(accessTokenID);
		draft.setAmount(amount);
		draft.setMobileNumber(toMobileNumber);
		draft.setFullname(markFullName);
		draft.setStatus(DraftTransaction.Status.CREATED);

		transactionRepo.saveP2PDraftTransaction(draft, accessToken.getAccessTokenID());

		return draft;
	}

	private VerifyTransferResponse verifyEwalletTransfer(String mobileNumber, BigDecimal amount, AccessToken accessToken) {

		SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());

		VerifyTransferRequest verifyRequest = new VerifyTransferRequest();
		verifyRequest.setChannelId(accessToken.getChannelID());
		verifyRequest.setAmount(amount);
		verifyRequest.setTarget(mobileNumber);
		verifyRequest.setSecurityContext(securityContext);

		String sourceMobileNumber = accessToken.getMobileNumber();

		if (sourceMobileNumber != null && sourceMobileNumber.equals(verifyRequest.getTarget())) {
			throw new ServiceInventoryWebException(Code.INVALID_TARGET_MOBILE_NUMBER, "Invalid target mobile number");
		}

		VerifyTransferResponse verifyResponse = ewalletProxy.verifyTransfer(verifyRequest);

		return verifyResponse;

	}

	@Override
	public P2PDraftTransaction getDraftTransactionDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		P2PDraftTransaction p2pDraftTransaction = transactionRepo.getP2PDraftTransaction(draftTransactionID, accessToken.getAccessTokenID());

		return p2pDraftTransaction;
	}

	@Override
	public OTP sendOTP(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		P2PDraftTransaction p2pDraftTransaction = getDraftTransactionDetails(draftTransactionID, accessTokenID);
		p2pDraftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		p2pDraftTransaction.setStatus(DraftTransaction.Status.OTP_SENT);

		transactionRepo.saveP2PDraftTransaction(p2pDraftTransaction, accessToken.getAccessTokenID());

		return otp;
	}

	@Override
	public DraftTransaction.Status confirmDraftTransaction(String draftTransactionID, OTP otp, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		P2PDraftTransaction p2pDraftTransaction = getDraftTransactionDetails(draftTransactionID, accessTokenID);

		otpService.isValidOTP(otp);

		p2pDraftTransaction.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		transactionRepo.saveP2PDraftTransaction(p2pDraftTransaction, accessToken.getAccessTokenID());

		P2PTransaction p2pTransaction = new P2PTransaction(p2pDraftTransaction);
		p2pTransaction.setStatus(Transaction.Status.VERIFIED);
		transactionRepo.saveP2PTransaction(p2pTransaction, accessToken.getAccessTokenID());

		performTransferMoney(accessToken, p2pTransaction);

		return p2pDraftTransaction.getStatus();
	}

	@Override
	public Transaction.Status getTransactionStatus(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		P2PTransaction p2pTransaction = getTransactionResult(transactionID, accessTokenID);
		Transaction.Status p2pTransactionStatus = p2pTransaction.getStatus();
		FailStatus failStatus = p2pTransaction.getFailStatus();

		if(p2pTransactionStatus == Transaction.Status.FAILED) {
			if (failStatus == FailStatus.UMARKET_FAILED) {
				throw new ServiceInventoryWebException(Code.CONFIRM_UMARKET_FAILED,
						"u-market confirmation processing fail.");
			} else if (failStatus == FailStatus.UNKNOWN_FAILED){
				throw new ServiceInventoryWebException(Code.CONFIRM_FAILED,
						"confirmation processing fail.");
			}
		}

		return p2pTransactionStatus;
	}

	@Override
	public P2PTransaction getTransactionResult(String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		P2PTransaction p2pTransaction = transactionRepo.getP2PTransaction(transactionID, accessToken.getAccessTokenID());

		return p2pTransaction;
	}

	private void performTransferMoney(AccessToken accessToken, P2PTransaction p2pTransaction) {
		asyncP2PTransferProcessor.transferEwallet(p2pTransaction, accessToken);
	}

	private String markFullName(String fullName)
	{
		String markName = "";

		fullName = fullName != null ? fullName.trim() : "";

		if (fullName == null || "".equals(fullName)) {
			markName = "-";
			return markName;
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

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAsyncP2PTransferProcessor(
			AsyncP2PTransferProcessor asyncP2PTransferProcessor) {
		this.asyncP2PTransferProcessor = asyncP2PTransferProcessor;
	}

}
