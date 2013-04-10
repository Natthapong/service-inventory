package th.co.truemoney.serviceinventory.bill.impl;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.bill.domain.BillPayment;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

@Service
public class AsyncBillPayProcessor {

	public Future<BillPayment> payBill(BillPayment billPaymentReceipt, AccessToken accessToken) {
		return null;
	}

}
