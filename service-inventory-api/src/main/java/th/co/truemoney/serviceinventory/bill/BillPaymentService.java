package th.co.truemoney.serviceinventory.bill;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BillPaymentService {

	public Bill getBillInformation(String barcode, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentDraft createBill(Bill billpayInfo, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentDraft getBillDetail(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public OTP sendOTP(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentDraft.Status confirmBill(String invoiceID, OTP otp, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction getBillPaymentResult(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

}
