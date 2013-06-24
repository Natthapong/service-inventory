package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.persona.proxies.TrueConvergentBillProxy;

public class TrueConvergenceOneBillPersona implements BillPersona {

    @Override
    public BillProxy getBillPayProxy() {
        return new TrueConvergentBillProxy();
    }

}
