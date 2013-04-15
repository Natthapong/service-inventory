package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
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

	public void saveBill(BillPaymentDraft bill, String accessTokenID);
	public BillPaymentDraft findBill(String billID, String accessTokenID);
	public void saveBillPayment(BillPaymentTransaction billPayment, String accessTokenID);
	public BillPaymentTransaction findBillPayment(String billPaymentID, String accessTokenID);

}
