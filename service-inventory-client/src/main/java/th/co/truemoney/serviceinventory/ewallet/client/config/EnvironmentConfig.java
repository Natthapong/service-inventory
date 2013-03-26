package th.co.truemoney.serviceinventory.ewallet.client.config;

public interface EnvironmentConfig {
	
	String getLoginUrl();
	String getLogoutUrl();
	String getUserDirectDebitSourceOfFundsUrl();
	String getUserProfileUrl();
	String getBalance();
	
	String getCreateTopUpQuoteFromDirectDebitUrl();
	String getTopUpQuoteDetailsUrl();
	String getRequestPlaceOrder();
	String getConfirmPlaceOrderUrl();
	String getTopUpOrderStatusUrl();
	String getTopUpOrderDetailsUrl();
}
