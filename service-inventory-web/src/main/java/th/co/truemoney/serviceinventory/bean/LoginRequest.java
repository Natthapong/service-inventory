package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;

public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 359449894890044786L;

	@NotNull @Valid
	private EWalletOwnerLogin userLogin;

	@NotNull @Valid
	private ClientLogin appLogin;

	@NotNull @Valid
	private ChannelInfo channelInfo;

	public LoginRequest() {
	}

	public LoginRequest(EWalletOwnerLogin userLogin, ClientLogin clientLogin,
			ChannelInfo channelInfo) {
		this.userLogin = userLogin;
		this.appLogin = clientLogin;
		this.channelInfo = channelInfo;
	}

	public EWalletOwnerLogin getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(EWalletOwnerLogin userLogin) {
		this.userLogin = userLogin;
	}

	public ClientLogin getAppLogin() {
		return appLogin;
	}

	public void setAppLogin(ClientLogin appLogin) {
		this.appLogin = appLogin;
	}

	public ChannelInfo getChannelInfo() {
		return channelInfo;
	}

	public void setChannelInfo(ChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
	}


}
