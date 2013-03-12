package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class Login implements Serializable {

	private static final long serialVersionUID = -8973296808315132605L;
	private String username;
	private String hashPassword;
	
	public Login() {
		
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
