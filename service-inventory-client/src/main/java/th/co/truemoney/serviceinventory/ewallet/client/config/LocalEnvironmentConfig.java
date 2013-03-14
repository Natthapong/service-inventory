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
		return "http://localhost:8585/service-inventory-web/v1/{username}/source-of-fund/direct-debits?channelID={channelID}&accessToken={accessToken}";
	}

}
