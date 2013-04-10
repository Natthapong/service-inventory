package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

public interface TransactionRepository {
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote, String accessTokenID);
	public TopUpQuote getTopUpEwalletDraftTransaction(String orderID, String accessTokenID);
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder, String accessTokenID);
	public TopUpOrder getTopUpEwalletTransaction(String orderID, String accessTokenID);

	public void saveP2PDraftTransaction(P2PDraftTransaction p2pDraftTransaction, String accessTokenID);
	public P2PDraftTransaction getP2PDraftTransaction(String p2pDraftTransactionID, String accessTokenID);
	public void saveP2PTransaction(P2PTransaction p2pTransaction, String accessTokenID);
	public P2PTransaction getP2PTransaction(String p2pTransactionID, String accessTokenID);

	public void saveBillInvoice(BillInvoice billInvoice, String accessTokenID);
	public BillInvoice getBillInvoice(String billInvoiceID, String accessTokenID);

	public void saveBillPayment(BillPayment billPayment, String accessTokenID);
	public BillPayment getBillPayment(String billPaymentID, String accessTokenID);

}
