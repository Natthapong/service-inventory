package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("local")
public class LocalEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}
	
	@Override
	public String getLogoutUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	@Override
	public String getUserDirectDebitSourceOfFundsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	@Override
	public String getUserProfileUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/getprofile/{accesstokenID}";
	}

	@Override
	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return "http://localhost:8585/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpQuoteDetailsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/quote/{quoteId}";
	}

	@Override
	public String getRequestPlaceOrder() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}?accessToken={accessToken}";
	}

	@Override
	public String getConfirmPlaceOrderUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}/confirm?accessToken={accessToken}";
	}

	@Override
	public String getTopUpOrderStatusUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}/status?accessToken={accessToken}";
	}

	@Override
	public String getTopUpOrderDetailsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}?accessToken={accessToken}";
	}
	
}
