package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface TmnProfileService {

	public String login(Integer channelId, Login login);

	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum);

	public void logout(String accessToken);
}

