package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}
	
	@Override
	public String getLogoutUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	@Override
	public String getUserDirectDebitSourceOfFundsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	@Override
	public String getUserProfileUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/getprofile/{accesstokenID}";
	}

	@Override
	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String getTopUpQuoteDetailsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/quote/{quoteId}";
	}

	@Override
	public String getRequestPlaceOrder() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{quoteId}?accessTokenID={accessTokenID}";
	}

	@Override
	public String getConfirmPlaceOrderUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}/confirm?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderStatusUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}/status?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderDetailsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}?accessTokenID={accessTokenID}";
	}
	
}
