package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ClientLogin implements Serializable {

	private static final long serialVersionUID = 4474849252035980061L;

	@NotNull
	private String appKey;

	@NotNull
	private String appUser;

	@NotNull
	private String appPassword;

	public ClientLogin() {
	}

	public ClientLogin(String appKey, String appUser, String appPassword) {
		this.appKey = appKey;
		this.appUser = appUser;
		this.appPassword = appPassword;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppUser() {
		return appUser;
	}

	public void setAppUser(String appUser) {
		this.appUser = appUser;
	}

	public String getAppPassword() {
		return appPassword;
	}

	public void setAppPassword(String appPassword) {
		this.appPassword = appPassword;
	}


}
