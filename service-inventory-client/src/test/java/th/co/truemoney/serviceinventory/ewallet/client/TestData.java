package th.co.truemoney.serviceinventory.ewallet.client;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;

public class TestData {

	public static Login createSuccessLogin() {
		return new Login("local@tmn.com", "password");
	}
	
	public static Login createAdamSuccessLogin(){
		return new Login("adam@tmn.com","adampassword");
	}
	
}
