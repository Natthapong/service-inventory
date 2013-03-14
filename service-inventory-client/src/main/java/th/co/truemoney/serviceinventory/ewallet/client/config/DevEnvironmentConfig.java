package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/login";
	}

}
