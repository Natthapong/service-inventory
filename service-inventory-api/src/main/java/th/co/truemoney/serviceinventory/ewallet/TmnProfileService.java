package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

@SuppressWarnings("rawtypes")
public interface TmnProfileService {
	
	public ServiceResponse<TmnProfile> login(Login login);
	
	public ServiceResponse extend(TmnProfile tmnProfile);

	public ServiceResponse logout(TmnProfile tmnProfile); 
	
}
