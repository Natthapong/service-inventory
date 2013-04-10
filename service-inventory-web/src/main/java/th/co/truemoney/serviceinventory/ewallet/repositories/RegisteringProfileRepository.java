package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface RegisteringProfileRepository {
	public void saveRegisteringProfile(TmnProfile tmnProfile);
	public TmnProfile getRegisteringProfile(String mobileNumber);
}
