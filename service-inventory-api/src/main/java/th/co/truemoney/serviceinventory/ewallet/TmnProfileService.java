package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TmnProfileService {

	public String login(Integer channelId, Login login) throws ServiceInventoryException;

	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum) throws ServiceInventoryException;

	public void logout(String accessToken) throws ServiceInventoryException;
}

