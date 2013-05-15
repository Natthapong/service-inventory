package th.co.truemoney.serviceinventory.ewallet.client.testutils;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

public class TestData {

	public static EWalletOwnerCredential createSuccessUserLogin() {
		return new EWalletOwnerCredential("local@tmn.com", "password", 40);
	}
	
	public static ClientCredential createSuccessClientLogin() {
		return new ClientCredential("myAppKey", "myAppUser", "myAppPassword", "iPhone", "iPhone");
	}

	public static EWalletOwnerCredential createAdamSuccessLogin(){
		return new EWalletOwnerCredential("adam@tmn.com","password", 40);
	}

	public static EWalletOwnerCredential createEveSuccessLogin(){
		return new EWalletOwnerCredential("eve@tmn.com","password", 40);
	}
	
	public static EWalletOwnerCredential createSimpsonsSuccessLogin(){
		return new EWalletOwnerCredential("simpson@tmn.com","password", 40);
	}
	
	public static Favorite createFavoriteBill() {

		Favorite favoriteBill = new Favorite();
		favoriteBill.setAmount(new BigDecimal(2000));
		favoriteBill.setRef1("555");
		favoriteBill.setServiceCode("tr");
		favoriteBill.setServiceType("billpay");

		return favoriteBill;
	}
}
