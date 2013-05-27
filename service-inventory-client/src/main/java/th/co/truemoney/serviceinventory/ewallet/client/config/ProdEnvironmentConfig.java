package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("prod")
public class ProdEnvironmentConfig {

	@Bean @Qualifier("endpoint.host")
	public String host() {
		return "https://PBISVC0102";
	}
}
