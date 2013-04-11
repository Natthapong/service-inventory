package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.billpay.proxy.impl.BillPayProxy;

public interface BarcodePersona {
	public BillPayProxy getBillPayProxyImpl();
}
