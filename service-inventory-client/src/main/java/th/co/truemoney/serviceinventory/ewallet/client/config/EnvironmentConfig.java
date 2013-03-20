package th.co.truemoney.serviceinventory.ewallet.client.config;

public interface EnvironmentConfig {
	
	String getLoginUrl();
	String getLogoutUrl();
	String getUserDirectDebitSourceOfFundsUrl();
	String getUserProfileUrl();
	
	String getCreateTopUpQuoteFromDirectDebitUrl();
	String getTopUpQuoteDetailsUrl();
	String getRequestPlaceOrder();
	String getConfirmPlaceOrderUrl();
	String getTopUpOrderStatusUrl();
	String getTopUpOrderDetailsUrl();
}
