package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitSource;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface TmnProfileService {
	
	public String login(Integer channelID, Login login);

	public List<DirectDebitSource> getDirectDebitSources(Integer ChannelID, String accessToken);	  
	
	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum, Integer channelID);
	
	public void Logout(String accessToken,Integer ChannelID);
}

