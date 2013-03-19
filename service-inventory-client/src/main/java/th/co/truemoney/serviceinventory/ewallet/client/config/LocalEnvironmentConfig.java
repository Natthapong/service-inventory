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
	public String getUserDirectDebitSourceOfFundsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	@Override
	public String getUserProfileUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/getprofile/{accesstokenID}/{checksum}";
	}

	@Override
	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return "http://localhost:8585/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}

}
