package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;

public interface TmnProfileService {
	
	public String login(Login login, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP);
	
}
