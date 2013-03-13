package th.co.truemoney.serviceinventory.service;

import th.co.truemoney.serviceinventory.domain.RequestBean;
import th.co.truemoney.serviceinventory.domain.ResponseBean;
import th.co.truemoney.serviceinventory.domain.SigninBean;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface LoginService {

	public String login(SigninBean signinBean) throws ServiceInventoryException;
	public ResponseBean<TmnProfile> getProfile(RequestBean requestBean) throws ServiceInventoryException;
	public void logout(Integer channelId, String token) throws ServiceInventoryException;
}