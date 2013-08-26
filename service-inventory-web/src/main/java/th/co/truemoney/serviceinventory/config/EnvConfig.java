package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import th.co.truemoney.serviceinventory.util.SecurityManager;

@Configuration
@PropertySource({"classpath:rsa_public.key"})
public class EnvConfig {

	@Autowired
	Environment env;
	
    @Bean 
    public SecurityManager securityManager() {
        return new SecurityManager();
    }
}
