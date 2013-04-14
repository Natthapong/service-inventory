package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.bill.domain.BillResponse;
import th.co.truemoney.serviceinventory.bill.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.bill.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.bill.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.bill.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.bill.exception.BillException;
import th.co.truemoney.serviceinventory.bill.proxy.impl.BillProxy;

public class TruemoveH implements BarcodePersona {

	@Override
	public BillProxy getBillPayProxyImpl() {

		return new BillProxy() {

			@Override
			public GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest) throws BillException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BillResponse verifyBillPay(VerifyBillPayRequest billPayRequest)
					throws BillException {
				BillResponse billResponse = new BillResponse();
				billResponse.setResultCode("0");
				return billResponse;
			}

			@Override
			public BillResponse confirmBillPay(ConfirmBillPayRequest billPayRequest)
					throws BillException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
