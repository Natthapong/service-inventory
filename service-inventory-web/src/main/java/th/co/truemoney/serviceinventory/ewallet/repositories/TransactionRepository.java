package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

public interface TransactionRepository {
	public void saveTopUpQuote(TopUpQuote topupQuote, String accessTokenID);
	public TopUpQuote findTopUpQuote(String orderID, String accessTokenID);
	public void saveTopUpOrder(TopUpOrder topupOrder, String accessTokenID);
	public TopUpOrder findTopUpOrder(String orderID, String accessTokenID);

	public void saveP2PTransferDraft(P2PTransferDraft p2pTransferDraft, String accessTokenID);
	public P2PTransferDraft findP2PTransferDraft(String p2pTransferDraftID, String accessTokenID);
	public void saveP2PTransferTransaction(P2PTransferTransaction p2pTransaction, String accessTokenID);
	public P2PTransferTransaction findP2PTransferTransaction(String p2pTransactionID, String accessTokenID);

	public void saveBill(Bill bill, String accessTokenID);
	public Bill findBill(String billID, String accessTokenID);
	public void saveBillPayment(BillPayment billPayment, String accessTokenID);
	public BillPayment fillBillPayment(String billPaymentID, String accessTokenID);

}
