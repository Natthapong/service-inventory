package th.co.truemoney.serviceinventory.persona;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnSecurityProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;

public interface Persona {
    public TmnProfileProxy  getTmnProfile();
    public TmnSecurityProxyClient getTmnSecurity();
    public BigDecimal getBalance();
    public void setBalance(BigDecimal balance);
    public TmnProfileProxyClient getTmnProfileClient();
    public WalletProxyClient getWalletProxyClient();
}
