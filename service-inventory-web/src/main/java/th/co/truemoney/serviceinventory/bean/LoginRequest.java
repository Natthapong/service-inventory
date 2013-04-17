package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;

public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 359449894890044786L;

	@NotNull @Valid
	private EWalletOwnerCredential userLogin;

	@NotNull @Valid
	private ClientCredential appLogin;

	public LoginRequest() {
	}

	public LoginRequest(EWalletOwnerCredential userLogin, ClientCredential clientLogin) {
		this.userLogin = userLogin;
		this.appLogin = clientLogin;
	}

	public EWalletOwnerCredential getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(EWalletOwnerCredential userLogin) {
		this.userLogin = userLogin;
	}

	public ClientCredential getAppLogin() {
		return appLogin;
	}

	public void setAppLogin(ClientCredential appLogin) {
		this.appLogin = appLogin;
	}

}
