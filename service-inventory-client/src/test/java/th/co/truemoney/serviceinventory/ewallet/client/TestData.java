package th.co.truemoney.serviceinventory.ewallet.client;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;

public class TestData {

	public static EWalletOwnerCredential createSuccessUserLogin() {
		return new EWalletOwnerCredential("local@tmn.com", "password", 40);
	}

	public static ClientCredential createSuccessClientLogin() {
		return new ClientCredential("myAppKey", "myAppUser", "myAppPassword", "iPhone", "iPhone");
	}

	public static EWalletOwnerCredential createAdamSuccessLogin(){
		return new EWalletOwnerCredential("adam@tmn.com","adampassword", 40);
	}


}
