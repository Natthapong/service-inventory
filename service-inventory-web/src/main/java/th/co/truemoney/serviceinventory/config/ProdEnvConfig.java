package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource({"classpath:rsa_public_prod.key"})
public class ProdEnvConfig {
	
}
