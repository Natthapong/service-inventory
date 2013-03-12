package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface TmnProfileService {
	
	public String login(Login login, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP);

	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum, Integer channelID);
	
}

