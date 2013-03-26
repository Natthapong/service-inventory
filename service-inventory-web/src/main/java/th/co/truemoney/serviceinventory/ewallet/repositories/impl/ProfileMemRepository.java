package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ProfileMemRepository implements ProfileRepository {

	public static HashMap<String, TmnProfile> profilesMap = new LinkedHashMap<String, TmnProfile>();

	@Override
	public void saveProfile(TmnProfile tmnProfile) {
		profilesMap.put(tmnProfile.getMobileno(), tmnProfile);
	}

	@Override
	public TmnProfile getTmnProfile(String mobileNo) {


		TmnProfile tmnProfile = profilesMap.get(mobileNo);
		if(tmnProfile == null) {
			throw new ServiceInventoryException(ServiceInventoryException.Code.PROFILE_NOT_FOUND,
					"profile not found.");
		}

		return tmnProfile;
	}

}
