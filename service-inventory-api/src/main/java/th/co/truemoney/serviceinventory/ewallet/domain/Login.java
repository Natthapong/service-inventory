package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Login implements Serializable {

	private static final long serialVersionUID = -8973296808315132605L;

	@NotNull
	private String username;

	@NotNull
	private String hashPassword;

	public Login() {
		super();
	}

	public Login(String username, String hashPassword) {
		this.username = username;
		this.hashPassword = hashPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashPassword() {
		return hashPassword;
	}

	public void setHashPassword(String hashPassword) {
		this.hashPassword = hashPassword;
	}

}
