package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;

public class TruemoveH implements BarcodePersona {

	@Override
	public BillProxy getBillPayProxyImpl() {

		return new BillProxy() {

			@Override
			public GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest) throws SIEngineException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SIEngineResponse verifyBillPay(VerifyBillPayRequest billPayRequest)
					throws SIEngineException {
				SIEngineResponse billResponse = new SIEngineResponse();
				billResponse.setResultCode("0");
				return billResponse;
			}

			@Override
			public SIEngineResponse confirmBillPay(ConfirmBillPayRequest billPayRequest)
					throws SIEngineException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
