package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Service
public class TopUpMobileServiceImpl implements TopUpMobileService {
	
	@Autowired
	private LegacyFacade legacyFacade;
	
	@Autowired
	private AccessTokenRepository accessTokenRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	AsyncTopUpMobileProcessor asyncTopUpMobileProcessor;

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setTransactionRepo(TransactionRepository transactionRepo) {
		this.transactionRepo = transactionRepo;
	}

	@Override
	public TopUpMobileDraft verifyAndCreateTopUpMobileDraft(
			String targetMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		//verify topup mobile
		TopUpMobile topUpMobile = legacyFacade.topUpMobile().verifyTopUpAirtime(targetMobileNumber, amount, accessToken);
		
		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft(UUID.randomUUID().toString(), topUpMobile, topUpMobile.getAmount(), topUpMobile.getID(), Status.CREATED);
		transactionRepo.saveTopUpMobileDraft(topUpMobileDraft, accessTokenID);
		return topUpMobileDraft;
	}

	@Override
	public TopUpMobileDraft getTopUpMobileDraftDetail(String draftID, String accessTokenID) 
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		TopUpMobileDraft topUpMobileDraft = transactionRepo.findTopUpMobileDraft(draftID, accessToken.getAccessTokenID());

		return topUpMobileDraft;
	}

	@Override
	public OTP sendOTP(String draftID, String accessTokenID)
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		OTP otp = otpService.send(accessToken.getMobileNumber());
				
		TopUpMobileDraft topUpMobileDraft = transactionRepo.findTopUpMobileDraft(draftID, accessTokenID);
		topUpMobileDraft.setOtpReferenceCode(otp.getReferenceCode());
		topUpMobileDraft.setStatus(TopUpMobileDraft.Status.OTP_SENT);
		
		transactionRepo.saveTopUpMobileDraft(topUpMobileDraft, accessTokenID);
		
		return otp;
	}

	@Override
	public Status confirmTopUpMobile(String draftID, OTP otp, String accessTokenID) 
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		TopUpMobileDraft topUpMobileDraft = transactionRepo.findTopUpMobileDraft(draftID, accessTokenID);

		otpService.isValidOTP(otp);

		topUpMobileDraft.setStatus(TopUpMobileDraft.Status.OTP_CONFIRMED);
		transactionRepo.saveTopUpMobileDraft(topUpMobileDraft, accessTokenID);

		TopUpMobileTransaction topUpMobileTransaction = new TopUpMobileTransaction(topUpMobileDraft);
		topUpMobileTransaction.setStatus(TopUpMobileTransaction.Status.VERIFIED);
		transactionRepo.saveTopUpMobileTransaction(topUpMobileTransaction, accessTokenID);

		performTopUpMobile(topUpMobileTransaction, accessToken);
		
		return topUpMobileDraft.getStatus();
	}

	private void performTopUpMobile(TopUpMobileTransaction topUpMobileTransaction, AccessToken accessToken) {
		asyncTopUpMobileProcessor.topUpMobile(topUpMobileTransaction, accessToken);		
	}

	@Override
	public th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status getTopUpMobileStatus(
			String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpMobileTransaction getTopUpMobileResult(String transactionID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}
}
