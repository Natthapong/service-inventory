package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EWalletOwnerLogin implements Serializable {

	private static final long serialVersionUID = -8973296808315132605L;

	@NotNull
	private String loginKey;

	@NotNull
	private String loginSecret;

	public EWalletOwnerLogin() {
		super();
	}

	public EWalletOwnerLogin(String loginKey, String hashPassword) {
		this.loginKey = loginKey;
		this.loginSecret = hashPassword;
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getLoginSecret() {
		return loginSecret;
	}

	public void setLoginSecret(String loginSecret) {
		this.loginSecret = loginSecret;
	}

}
