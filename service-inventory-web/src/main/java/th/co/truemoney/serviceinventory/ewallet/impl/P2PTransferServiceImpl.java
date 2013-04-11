package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction.FailStatus;

@Service
public class P2PTransferServiceImpl implements P2PTransferService {
	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	private AsyncP2PTransferProcessor asyncP2PTransferProcessor;

	@Autowired
	private OTPService otpService;

	@Override
	public P2PDraftTransaction verifyAndCreateTransferDraft(String targetMobileNumber, BigDecimal amount, String accessTokenID) {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		//--- Send to verify amount ---//
		String targetName = legacyFacade.transfer(amount)
						.fromChannelID(accessToken.getChannelID())
						.fromUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
						.toTargetUser(targetMobileNumber)
						.verify();

		String targetMarkedFullName = markFullName(targetName);

		P2PDraftTransaction draft = createP2PDraft(amount, targetMobileNumber, targetMarkedFullName, accessTokenID);
		transactionRepo.saveP2PDraftTransaction(draft, accessToken.getAccessTokenID());

		return draft;
	}

	@Override
	public P2PDraftTransaction getTransferDraftDetails(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		P2PDraftTransaction p2pDraftTransaction = transactionRepo.getP2PDraftTransaction(draftTransactionID, accessToken.getAccessTokenID());

		return p2pDraftTransaction;
	}

	@Override
	public OTP submitTransferral(String draftTransactionID, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		P2PDraftTransaction p2pDraftTransaction = getTransferDraftDetails(draftTransactionID, accessTokenID);
		p2pDraftTransaction.setOtpReferenceCode(otp.getReferenceCode());
		p2pDraftTransaction.setStatus(DraftTransaction.Status.OTP_SENT);

		transactionRepo.saveP2PDraftTransaction(p2pDraftTransaction, accessToken.getAccessTokenID());

		return otp;
	}

	@Override
	public P2PDraftTransaction.Status verifyOTPAndPerformTransferring(String draftTransactionID, OTP otp, String accessTokenID)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		P2PDraftTransaction p2pDraftTransaction = getTransferDraftDetails(draftTransactionID, accessTokenID);

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
	public Transaction.Status getTransferingStatus(String transactionID, String accessTokenID)
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
		return transactionRepo.getP2PTransaction(transactionID, accessToken.getAccessTokenID());
	}

	private P2PDraftTransaction createP2PDraft(BigDecimal amount, String targetMobileNumber, String targetName, String byAccessToken) {
		String draftID = UUID.randomUUID().toString();
		P2PDraftTransaction draft = new P2PDraftTransaction();
		draft.setID(draftID);
		draft.setAccessTokenID(byAccessToken);
		draft.setAmount(amount);
		draft.setMobileNumber(targetMobileNumber);
		draft.setFullname(targetName);
		draft.setStatus(DraftTransaction.Status.CREATED);

		return draft;
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

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
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
