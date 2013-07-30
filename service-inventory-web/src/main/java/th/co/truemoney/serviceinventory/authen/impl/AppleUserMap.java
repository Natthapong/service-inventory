package th.co.truemoney.serviceinventory.authen.impl;

import java.io.Serializable;
import java.util.Map;

public class AppleUserMap implements Serializable {
	
	private static final long serialVersionUID = 1739064702417943969L;
	
	public Map<String, AppleUser> appleUsers;

	public Map<String, AppleUser> getAppleUsers() {
		return appleUsers;
	}

	public void setAppleUsers(Map<String, AppleUser> appleUsers) {
		this.appleUsers = appleUsers;
	}

}
