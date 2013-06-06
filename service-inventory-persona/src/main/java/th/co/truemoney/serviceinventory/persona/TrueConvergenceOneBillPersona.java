package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.persona.proxies.TopUpTruemoneyProxy;
import th.co.truemoney.serviceinventory.persona.proxies.TrueConvergentBillProxy;

public class TrueConvergenceOneBillPersona implements BarcodePersona {

    @Override
    public BillProxy getBillPayProxy() {
        return new TrueConvergentBillProxy();
    }

    @Override
    public TopUpMobileProxy getTopUpMobileProxy() {
        return new TopUpTruemoneyProxy();
    }


}
