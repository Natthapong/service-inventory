package th.co.truemoney.serviceinventory.ewallet.client.config;

public class DevEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/login";
	}

}
