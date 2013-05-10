package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;

public interface BarcodePersona {
    public BillProxy getBillPayProxy();
    public TopUpMobileProxy getTopUpMobileProxy();
}
