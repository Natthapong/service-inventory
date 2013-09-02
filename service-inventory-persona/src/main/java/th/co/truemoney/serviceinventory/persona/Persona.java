package th.co.truemoney.serviceinventory.persona;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;

public interface Persona {
    public BigDecimal getBalance();
    public void setBalance(BigDecimal balance);
    public TmnSecurityProxyClient getTmnSecurityClient();
    public TmnProfileProxyClient getTmnProfileClient();
    public WalletProxyClient getWalletProxyClient();
}
