package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ChangePassword implements Serializable {

	private static final long serialVersionUID = -5818001071967226624L;
	private String oldPassword;
	private String password;
	
	public ChangePassword() {
		super();
	}

	public ChangePassword(String oldPassword, String password) {
		super();
		this.oldPassword = oldPassword;
		this.password = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}	
	
}
