package th.co.truemoney.serviceinventory.bill.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class BillPaymentServiceImpl implements  BillPaymentService {

	@Autowired
	private OTPService otpService;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private BillInformationRepository billInfoRepo;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	AsyncBillPayProcessor asyncBillPayProcessor;

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepo(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setTransactionRepository(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;

	}
	@Override
	public Bill retrieveBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		Bill bill = legacyFacade.billing()
								.readBillInfo(barcode)
								   .fromApp("MOBILE_IPHONE", "IPHONE+1", "f7cb0d495ea6d989")
								   .fromBillChannel("iPhone", "iPhone")
								   .getInformation();

		bill.setID(UUID.randomUUID().toString());
		billInfoRepo.saveBill(bill, accessTokenID);

		return bill;

	}

	@Override
	public BillPaymentDraft verifyPaymentAbility(String billID, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {

		Bill billInfo = billInfoRepo.findBill(billID, accessTokenID);
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		//verify bill.
		String verificationID = legacyFacade.billing()
						.fromBill(billInfo.getRef1(), billInfo.getRef2(), billInfo.getTarget())
							.aUser(accessToken.getSessionID(), accessToken.getTruemoneyID())
							.usingMobilePayPoint(accessToken.getMobileNumber())
							.fromApp("MOBILE_IPHONE", "IPHONE+1", "f7cb0d495ea6d989")
							.fromBillChannel("iPhone", "iPhone")
							.paying(amount, billInfo.getServiceFee().calculateFee(amount), billInfo.getEwalletSourceOfFund().calculateFee(amount))
							.verifyPayment();

		BillPaymentDraft billDraft = new BillPaymentDraft(UUID.randomUUID().toString(), billInfo, amount, verificationID, Status.CREATED);
		transactionRepository.saveBillPaymentDraft(billDraft, accessTokenID);
		return billDraft;
	}

	@Override
	public BillPaymentDraft getBillPaymentDraftDetail(String invoiceID, String accessTokenID) throws ServiceInventoryException {
		return transactionRepository.findBillPaymentDraft(invoiceID, accessTokenID);
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		BillPaymentDraft billInvoice = transactionRepository.findBillPaymentDraft(invoiceID, accessTokenID);
		billInvoice.setOtpReferenceCode(otp.getReferenceCode());
		billInvoice.setStatus(BillPaymentDraft.Status.OTP_SENT);

		transactionRepository.saveBillPaymentDraft(billInvoice, accessTokenID);

		return otp;
	}

	@Override
	public BillPaymentDraft.Status confirmBill(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		BillPaymentDraft invoiceDetails = getBillPaymentDraftDetail(invoiceID, accessTokenID);

		otpService.isValidOTP(otp);

		invoiceDetails.setStatus(BillPaymentDraft.Status.OTP_CONFIRMED);
		transactionRepository.saveBillPaymentDraft(invoiceDetails, accessTokenID);

		BillPaymentTransaction billPaymentReceipt = new BillPaymentTransaction(invoiceDetails);
		billPaymentReceipt.setStatus(BillPaymentTransaction.Status.VERIFIED);
		transactionRepository.saveBillPaymentTransaction(billPaymentReceipt, accessTokenID);

		performBillPay(billPaymentReceipt, accessToken);

		return invoiceDetails.getStatus();
	}

	private void performBillPay(BillPaymentTransaction billPaymentReceipt, AccessToken accessToken) {
		asyncBillPayProcessor.payBill(billPaymentReceipt, accessToken);
	}

	@Override
	public BillPaymentTransaction.Status getBillPaymentStatus(
			String billPaymentID, String accessTokenID)
			throws ServiceInventoryException {

		BillPaymentTransaction billPayment = getBillPaymentResult(billPaymentID, accessTokenID);
		BillPaymentTransaction.Status paymentStatus = billPayment.getStatus();
//		FailStatus failStatus = topUpOrder.getFailStatus();

//		if(topUpStatus == BillPaymentTransaction.Status.FAILED) {
//			if (failStatus == FailStatus.BANK_FAILED) {
//				throw new ServiceInventoryWebException(Code.CONFIRM_BANK_FAILED,
//						"bank confirmation processing fail.");
//			} else if (failStatus == FailStatus.UMARKET_FAILED) {
//				throw new ServiceInventoryWebException(Code.CONFIRM_UMARKET_FAILED,
//						"u-market confirmation processing fail.");
//			} else {
//				throw new ServiceInventoryWebException(Code.CONFIRM_FAILED,
//						"confirmation processing fail.");
//			}
//		}

		return paymentStatus;

	}

	@Override
	public BillPaymentTransaction getBillPaymentResult(String billPaymentID, String accessTokenID) throws ServiceInventoryException {
		return transactionRepository.findBillPaymentTransaction(billPaymentID, accessTokenID);
	}

	public void setAsyncBillPayProcessor(AsyncBillPayProcessor asyncBillPayProcessor) {
		this.asyncBillPayProcessor = asyncBillPayProcessor;
	}

}
