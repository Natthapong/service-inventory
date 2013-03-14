package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface TmnProfileService {
	
	public String login(Integer channelID, Login login);

	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum, Integer channelID);
	
	public void Logout(String accessToken,Integer ChannelID);
}

