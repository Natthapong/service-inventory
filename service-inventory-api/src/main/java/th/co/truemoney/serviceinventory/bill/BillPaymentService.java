package th.co.truemoney.serviceinventory.bill;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface BillPaymentService {

	public Bill retrieveBillInformationWithBarcode(String barcode, String accessTokenID)
			throws ServiceInventoryException;

	public Bill retrieveBillInformationWithBillCode(String billCode, String ref1, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;
	
	public Bill retrieveBillInformationWithKeyin(String billCode, String accessTokenID)
			throws ServiceInventoryException;
	
	public Bill updateBillInformation(String billCode, String ref1, String ref2, BigDecimal amount, String accessTokenID)
		throws ServiceInventoryException;
	
	public BillPaymentDraft verifyPaymentAbility(String billID, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentDraft getBillPaymentDraftDetail(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction.Status performPayment(String invoiceID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction.Status getBillPaymentStatus(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

	public BillPaymentTransaction getBillPaymentResult(String billPaymentID, String accessTokenID)
			throws ServiceInventoryException;

}
