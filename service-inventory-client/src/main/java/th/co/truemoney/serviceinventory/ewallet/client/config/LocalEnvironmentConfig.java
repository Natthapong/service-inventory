package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalEnvironmentConfig {

	@Bean @Qualifier("endpoint.host")
	public String host() {
		return "http://localhost:8585";
	}

}
