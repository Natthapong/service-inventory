package th.co.truemoney.serviceinventory.bill.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
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
	public BillInfo getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		return legacyFacade.billPayment()
						   .withBarcode(barcode)
						   .fromApp("MOBILE_IPHONE", "IPHONE+1", "f7cb0d495ea6d989")
						   .fromBillChannel("iPhone", "iPhone Application")
						   .getInformation();
	}

	@Override
	public Bill createBill(BillInfo billpayInfo, String accessTokenID)
			throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		//verify bill.
		legacyFacade.billPayment()
					.fromAccessToken(accessToken)
					.fromApp("MOBILE_IPHONE", "IPHONE+1", "f7cb0d495ea6d989")
				    .fromBillChannel("iPhone", "iPhone Application")
					.verify(billpayInfo);

		String invoiceID = UUID.randomUUID().toString();
		Bill billInvoice = new Bill(invoiceID, Bill.Status.CREATED, billpayInfo);

		//save bill.
		transactionRepository.saveBill(billInvoice, accessTokenID);

		return billInvoice;
	}

	@Override
	public Bill getBillDetail(String invoiceID, String accessTokenID) throws ServiceInventoryException {
		return transactionRepository.findBill(invoiceID, accessTokenID);
	}

	@Override
	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException {

		// --- Get Account Detail from accessToken ---//
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		OTP otp = otpService.send(accessToken.getMobileNumber());

		Bill billInvoice = transactionRepository.findBill(invoiceID, accessTokenID);
		billInvoice.setOtpReferenceCode(otp.getReferenceCode());

		transactionRepository.saveBill(billInvoice, accessTokenID);

		return otp;
	}

	@Override
	public Bill.Status confirmBill(String invoiceID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		Bill invoiceDetails = getBillDetail(invoiceID, accessTokenID);

		otpService.isValidOTP(otp);

		invoiceDetails.setStatus(Bill.Status.OTP_CONFIRMED);
		transactionRepository.saveBill(invoiceDetails, accessTokenID);

		BillPayment billPaymentReceipt = new BillPayment(invoiceDetails);
		billPaymentReceipt.setStatus(BillPayment.Status.VERIFIED);
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
