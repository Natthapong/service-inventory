package th.co.truemoney.serviceinventory.bill.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class BillPaymentServiceImpl implements  BillPaymentService {

	@Autowired
	private OTPService otpService;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private TransactionRepository transactionRepository;

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

	@Override
	public BillPaymentInfo getBillInformation(String barcode,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillInvoice createBillInvoice(BillPaymentInfo billpayInfo,
			String accessTokenID) throws ServiceInventoryException {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		//verify bill.
		String invoiceID = UUID.randomUUID().toString();
		
		BillInvoice billInvoice = new BillInvoice(billpayInfo);
		billInvoice.setID(invoiceID);
		billInvoice.setStatus(Status.CREATED);
		
		//save bill.
		transactionRepository.saveBillInvoice(billInvoice, accessTokenID);

		return billInvoice;
	}

	@Override
	public BillInvoice getBillInvoiceDetail(String invoiceID, String accessTokenID) throws ServiceInventoryException {
		return transactionRepository.getBillInvoice(invoiceID, accessTokenID);
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		
		OTP otp = otpService.send(accessToken.getMobileNumber());

		BillInvoice billInvoice = transactionRepository.getBillInvoice(invoiceID, accessTokenID);
		billInvoice.setOtpReferenceCode(otp.getReferenceCode());
		
		transactionRepository.saveBillInvoice(billInvoice, accessTokenID);
		
		return otp;
	}

	@Override
	public Status confirmBillInvoice(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		BillInvoice invoiceDetails = getBillInvoiceDetail(invoiceID, accessTokenID);

		otpService.isValidOTP(otp);

		invoiceDetails.setStatus(DraftTransaction.Status.OTP_CONFIRMED);
		transactionRepository.saveBillInvoice(invoiceDetails, accessTokenID);

		BillPayment billPaymentReceipt = new BillPayment(invoiceDetails);
		billPaymentReceipt.setStatus(Transaction.Status.VERIFIED);
		transactionRepository.saveBillPayment(billPaymentReceipt, accessTokenID);

		performBillPay(billPaymentReceipt, accessToken);

		return invoiceDetails.getStatus();
	}

	private void performBillPay(BillPayment billPaymentReceipt, AccessToken accessToken) {
		asyncBillPayProcessor.payBill(billPaymentReceipt, accessToken);
	}

	@Override
	public th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status getBillPaymentStatus(
			String billPaymentID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillPayment getBillPaymentResult(String billPaymentID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAsyncBillPayProcessor(AsyncBillPayProcessor asyncBillPayProcessor) {
		this.asyncBillPayProcessor = asyncBillPayProcessor;
	}
}
