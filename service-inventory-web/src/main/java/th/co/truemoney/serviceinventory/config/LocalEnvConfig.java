package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("local")
@PropertySource({"classpath:rsa_public.key"})
public class LocalEnvConfig {
	
}
