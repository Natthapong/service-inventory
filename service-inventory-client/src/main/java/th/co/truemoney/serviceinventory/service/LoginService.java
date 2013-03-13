package th.co.truemoney.serviceinventory.service;

import th.co.truemoney.serviceinventory.domain.RequestBean;
import th.co.truemoney.serviceinventory.domain.ResponseBean;
import th.co.truemoney.serviceinventory.domain.SigninBean;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;

public interface LoginService {

	public String login(SigninBean signinBean);
	public ResponseBean<TmnProfile> getProfile(RequestBean requestBean);
	
}