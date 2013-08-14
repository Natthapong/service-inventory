package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;

public class BillPaymentOptionsBuilder {

	private BillPaymentHandler billPaymentFacade;

	public BillPaymentOptionsBuilder(BillPaymentHandler billPaymentFacade) {
		this.billPaymentFacade = billPaymentFacade;
	}

	public GetBillInfoBuilder readBillInfoWithBarcode(String barcode) {
		return new GetBillInfoBuilder(billPaymentFacade).withBarcode(barcode);
	}

	public GetBillInfoBuilder readBillInfoWithBillCode(String billCode) {
		return new GetBillInfoBuilder(billPaymentFacade).withBillCode(billCode);
	}

	public GetBillInfoBuilder readBillOutStandingOnlineWithBillCode(String billCode) {
		return new GetBillInfoBuilder(billPaymentFacade).withBillCode(billCode);
	}

	public BillPaymentBuilder fromBill(String ref1, String ref2, String targetAgent) {
		return new BillPaymentBuilder(billPaymentFacade).forBillBarcode(ref1, ref2, targetAgent);
	}

}