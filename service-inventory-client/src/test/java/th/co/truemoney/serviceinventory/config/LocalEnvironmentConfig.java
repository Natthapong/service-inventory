package th.co.truemoney.serviceinventory.config;

import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;

public class LocalEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "http://localhost:8585/service-inventory-web/v1/login";
	}

}
