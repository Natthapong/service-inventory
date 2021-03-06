package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class ProfileMemoryRepository implements RegisteringProfileRepository {

	public Map<String, TmnProfile> profilesMap = new LinkedHashMap<String, TmnProfile>();

	@Override
	public void saveRegisteringProfile(TmnProfile tmnProfile) {
		profilesMap.put(tmnProfile.getMobileNumber(), tmnProfile);
	}

	@Override
	public TmnProfile findRegisteringProfileByMobileNumber(String mobileNumber) {
		if (profilesMap.get(mobileNumber) == null) {
			throw new ResourceNotFoundException(Code.PROFILE_NOT_FOUND, "profile not found.");
		}
		return profilesMap.get(mobileNumber);
	}

}
