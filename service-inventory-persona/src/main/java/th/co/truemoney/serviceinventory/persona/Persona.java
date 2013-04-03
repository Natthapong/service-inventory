package th.co.truemoney.serviceinventory.persona;

import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.TmnSecurityProxy;

public interface Persona {
	public TmnProfileProxy  getTmnProfile();
	public TmnSecurityProxy getTmnSecurity();
}