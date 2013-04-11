package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

public class TransactionMemoryRepository implements TransactionRepository {

	public HashMap<String, TopUpQuote> quotesMap = new LinkedHashMap<String, TopUpQuote>();
	public HashMap<String, TopUpOrder> ordersMap = new LinkedHashMap<String, TopUpOrder>();
	public HashMap<String, P2PDraftTransaction> p2pDraftTransactionMap = new LinkedHashMap<String, P2PDraftTransaction>();
	public HashMap<String, P2PTransaction> p2pTransactionMap = new LinkedHashMap<String, P2PTransaction>();
	public HashMap<String, BillInvoice> billInvoiceMap = new LinkedHashMap<String, BillInvoice>();
	public HashMap<String, BillPayment> billPaymentMap = new LinkedHashMap<String, BillPayment>();

	@Override
	public void saveTopUpEwalletDraftTransaction(TopUpQuote topupQuote,
			String accessTokenID) {
		quotesMap.put(accessTokenID + ":" + topupQuote.getID(), topupQuote);
	}

	@Override
	public TopUpQuote getTopUpEwalletDraftTransaction(String quoteID,
			String accessTokenID) {
		TopUpQuote topUpQuote = quotesMap.get(accessTokenID + ":" + quoteID);

		if (topUpQuote == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND, "TopUp quote not found.");
		}

		return topUpQuote;
	}

	@Override
	public void saveTopUpEwalletTransaction(TopUpOrder topupOrder,
			String accessTokenID) {
		ordersMap.put(accessTokenID + ":" + topupOrder.getID(), topupOrder);
	}

	@Override
	public TopUpOrder getTopUpEwalletTransaction(String orderID,
			String accessTokenID) throws ServiceInventoryWebException {
		TopUpOrder topUpOrder = ordersMap.get(accessTokenID + ":" + orderID);
		if (topUpOrder == null) {
			throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND,
					"TopUp order not found.");
		}
		return topUpOrder;
	}

	@Override
	public void saveP2PDraftTransaction(
			P2PDraftTransaction p2pDraftTransaction, String accessTokenID) {
		p2pDraftTransactionMap.put(
				accessTokenID + ":" + p2pDraftTransaction.getID(),
				p2pDraftTransaction);
	}

	@Override
	public P2PDraftTransaction getP2PDraftTransaction(
			String p2pDraftTransactionID, String accessTokenID) {
		P2PDraftTransaction p2pDraftTransaction = p2pDraftTransactionMap
				.get(accessTokenID + ":" + p2pDraftTransactionID);

		if (p2pDraftTransaction == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND,
					"Draft transfer transaction not found.");
		}

		return p2pDraftTransaction;
	}

	@Override
	public void saveP2PTransaction(P2PTransaction p2pTransaction,
			String accessTokenID) {
		p2pTransactionMap.put(accessTokenID + ":" + p2pTransaction.getID(),
				p2pTransaction);
	}

	@Override
	public P2PTransaction getP2PTransaction(String p2pTransactionID,
			String accessTokenID) {
		P2PTransaction p2pTransaction = p2pTransactionMap.get(accessTokenID
				+ ":" + p2pTransactionID);
		if (p2pTransaction == null) {
			throw new ResourceNotFoundException(Code.TRANSACTION_NOT_FOUND,
					"Transfer transaction not found.");
		}
		return p2pTransaction;
	}

	@Override
	public void saveBillInvoice(BillInvoice billInvoice, String accessTokenID) {
		billInvoiceMap.put(accessTokenID + ":" + billInvoice.getID(), billInvoice);
	}

	@Override
	public BillInvoice getBillInvoice(String billInvoiceID,
			String accessTokenID) {

		BillInvoice billInvoice = billInvoiceMap.get(accessTokenID + ":" + billInvoiceID);

		if (billInvoice == null) {
			throw new ResourceNotFoundException(
					Code.DRAFT_TRANSACTION_NOT_FOUND,
					"bill invoice not found.");
		}

		return billInvoice;
	}

	@Override
	public void saveBillPayment(BillPayment billPayment, String accessTokenID) {
		billPaymentMap.put(accessTokenID + ":" + billPayment.getID(), billPayment);
	}

	@Override
	public BillPayment getBillPayment(String billPaymentID, String accessTokenID) {

		BillPayment billPayment = billPaymentMap.get(accessTokenID + ":" + billPaymentID);

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
		p2pDraftTransactionMap.clear();
		p2pTransactionMap.clear();
		billInvoiceMap.clear();
		billPaymentMap.clear();
	}

}
