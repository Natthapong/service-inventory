package th.co.truemoney.serviceinventory.bill;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BillPaymentService {
	
	public BillPaymentInfo getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException;
			
	public BillInvoice createBillInvoice(BillPaymentInfo billpayInfo, String accessTokenID)
			throws ServiceInventoryException;

	public BillInvoice getBillInvoiceDetail(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public DraftTransaction.Status confirmBillInvoice(String invoiceID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public Transaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPayment getBillPaymentResult(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;
	
}
