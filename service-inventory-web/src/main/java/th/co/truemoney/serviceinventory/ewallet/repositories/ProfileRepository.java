package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface ProfileRepository {	
	public void saveProfile(TmnProfile tmnProfile);
	public TmnProfile getTmnProfile(String mobileNo);
}
