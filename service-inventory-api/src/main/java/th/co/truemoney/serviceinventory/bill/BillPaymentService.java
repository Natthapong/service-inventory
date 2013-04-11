package th.co.truemoney.serviceinventory.bill;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BillPaymentService {

	public BillInfo getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException;

	public Bill createBill(BillInfo billpayInfo, String accessTokenID)
			throws ServiceInventoryException;

	public Bill getBillDetail(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public DraftTransaction.Status confirmBill(String invoiceID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public Transaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPayment getBillPaymentResult(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

}
