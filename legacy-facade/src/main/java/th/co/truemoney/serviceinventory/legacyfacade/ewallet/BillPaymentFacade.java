package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;

public class BillPaymentFacade {
	
	public BillPaymentInfo getBarcodeInformation(String barcode) {		
		
		return new BillPaymentInfo();
	}
	
	public BillPaymentInfo verify(BillPaymentInfo billpayInfo) {
		// parse billpayInfo to billpayRequest + functionID
		
		// parse obj to str xml and call billpay service
		
		// check billpayResponse result_code="0"

		return null;
	}
}
