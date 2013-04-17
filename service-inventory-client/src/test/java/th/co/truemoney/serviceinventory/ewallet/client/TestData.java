package th.co.truemoney.serviceinventory.ewallet.client;

import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;

public class TestData {

	public static EWalletOwnerLogin createSuccessUserLogin() {
		return new EWalletOwnerLogin("local@tmn.com", "password");
	}

	public static ClientLogin createSuccessClientLogin() {
		return new ClientLogin("myAppKey", "myAppUser", "myAppPassword");
	}

	public static EWalletOwnerLogin createAdamSuccessLogin(){
		return new EWalletOwnerLogin("adam@tmn.com","adampassword");
	}

	public static ChannelInfo createSuccessChannelInfo() {
		return new ChannelInfo(40, "mobile", "mobile ios");
	}

}
