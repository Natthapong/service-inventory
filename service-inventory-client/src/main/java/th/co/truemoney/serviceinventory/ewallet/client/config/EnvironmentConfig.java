package th.co.truemoney.serviceinventory.ewallet.client.config;

public interface EnvironmentConfig {
	String getLoginUrl();
	String getUserDirectDebitSourceOfFundsUrl();
	String getUserProfileUrl();
	String getCreateTopUpQuoteFromDirectDebitUrl();
}
