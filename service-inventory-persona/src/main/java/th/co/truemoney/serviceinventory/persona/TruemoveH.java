package th.co.truemoney.serviceinventory.persona;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import th.co.truemoney.serviceinventory.billpay.domain.BillPayRequest;
import th.co.truemoney.serviceinventory.billpay.domain.BillPayResponse;
import th.co.truemoney.serviceinventory.billpay.proxy.impl.BillPayProxy;

public class TruemoveH implements BarcodePersona{

	@Override
	public BillPayProxy getBillPayProxyImpl() {
		
		return new BillPayProxy() {
			
			@Override
			public BillPayResponse verifyBillPay(BillPayRequest billPayRequest)
					throws JAXBException, IOException {
				BillPayResponse billPayResponse = new BillPayResponse();
				billPayResponse.setResultCode("0");
				return billPayResponse;
			}
			
			@Override
			public BillPayResponse getBarcodeInformation(
					BillPayRequest billPaymentInfoRequest) throws JAXBException,
					IOException {
				
				return null;
			}
		};
	}
	
}
