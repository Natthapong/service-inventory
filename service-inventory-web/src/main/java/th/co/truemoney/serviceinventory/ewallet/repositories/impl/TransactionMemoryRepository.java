package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

public class TransactionMemoryRepository implements TransactionRepository {

	public HashMap<String, TopUpQuote> quotesMap = new LinkedHashMap<String, TopUpQuote>();
	public HashMap<String, TopUpOrder> ordersMap = new LinkedHashMap<String, TopUpOrder>();
	public HashMap<String, P2PTransferDraft> p2pTransferDraftMap = new LinkedHashMap<String, P2PTransferDraft>();
	public HashMap<String, P2PTransferTransaction> p2pTransactionMap = new LinkedHashMap<String, P2PTransferTransaction>();
	public HashMap<String, BillPaymentDraft> billInvoiceMap = new LinkedHashMap<String, BillPaymentDraft>();
	public HashMap<String, BillPaymentTransaction> billPaymentMap = new LinkedHashMap<String, BillPaymentTransaction>();

	@Override
	public void saveTopUpQuote(TopUpQuote topupQuote,
			String accessTokenID) {
		quotesMap.put(accessTokenID + ":" + topupQuote.getID(), topupQuote);
	}

	@Override
	public TopUpQuote findTopUpQuote(String quoteID,
			String accessTokenID) {
		TopUpQuote topUpQuote = quotesMap.get(accessTokenID + ":" + quoteID);

		if (topUpQuote == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND, "TopUp quote not found.");
		}

		return topUpQuote;
	}

	@Override
	public void saveTopUpOrder(TopUpOrder topupOrder,
			String accessTokenID) {
		ordersMap.put(accessTokenID + ":" + topupOrder.getID(), topupOrder);
	}

	@Override
	public TopUpOrder findTopUpOrder(String orderID,
			String accessTokenID) throws ServiceInventoryWebException {
		TopUpOrder topUpOrder = ordersMap.get(accessTokenID + ":" + orderID);
		if (topUpOrder == null) {
			throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND,
					"TopUp order not found.");
		}
		return topUpOrder;
	}

	@Override
	public void saveP2PTransferDraft(
			P2PTransferDraft p2pTransferDraft, String accessTokenID) {
		p2pTransferDraftMap.put(
				accessTokenID + ":" + p2pTransferDraft.getID(),
				p2pTransferDraft);
	}

	@Override
	public P2PTransferDraft findP2PTransferDraft(
			String p2pTransferDraftID, String accessTokenID) {
		P2PTransferDraft p2pTransferDraft = p2pTransferDraftMap
				.get(accessTokenID + ":" + p2pTransferDraftID);

		if (p2pTransferDraft == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND,
					"Draft transfer transaction not found.");
		}

		return p2pTransferDraft;
	}

	@Override
	public void saveP2PTransferTransaction(P2PTransferTransaction p2pTransaction,
			String accessTokenID) {
		p2pTransactionMap.put(accessTokenID + ":" + p2pTransaction.getID(),
				p2pTransaction);
	}

	@Override
	public P2PTransferTransaction findP2PTransferTransaction(String p2pTransactionID,
			String accessTokenID) {
		P2PTransferTransaction p2pTransaction = p2pTransactionMap.get(accessTokenID
				+ ":" + p2pTransactionID);
		if (p2pTransaction == null) {
			throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND,
					"Transfer transaction not found.");
		}
		return p2pTransaction;
	}

	@Override
	public void saveBill(BillPaymentDraft billInvoice, String accessTokenID) {
		billInvoiceMap.put(accessTokenID + ":" + billInvoice.getID(), billInvoice);
	}

	@Override
	public BillPaymentDraft findBill(String billInvoiceID,
			String accessTokenID) {

		BillPaymentDraft billInvoice = billInvoiceMap.get(accessTokenID + ":" + billInvoiceID);

		if (billInvoice == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND,
					"bill invoice not found.");
		}

		return billInvoice;
	}

	@Override
	public void saveBillPayment(BillPaymentTransaction billPayment, String accessTokenID) {
		billPaymentMap.put(accessTokenID + ":" + billPayment.getID(), billPayment);
	}

	@Override
	public BillPaymentTransaction findBillPayment(String billPaymentID, String accessTokenID) {

		BillPaymentTransaction billPayment = billPaymentMap.get(accessTokenID + ":" + billPaymentID);

		if (billPayment == null) {
			throw new ResourceNotFoundException(
					Code.TRANSACTION_NOT_FOUND,
					"biill payment not found.");
		}

		return billPayment;
	}

	public void clear() {
		quotesMap.clear();
		ordersMap.clear();
		p2pTransferDraftMap.clear();
		p2pTransactionMap.clear();
		billInvoiceMap.clear();
		billPaymentMap.clear();
	}

}
