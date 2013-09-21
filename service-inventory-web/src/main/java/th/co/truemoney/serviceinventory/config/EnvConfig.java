package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import th.co.truemoney.serviceinventory.util.SecurityManager;

@Configuration
@Import({LocalEnvConfig.class, DevEnvConfig.class, ProdEnvConfig.class})
public class EnvConfig {

	@Autowired
	Environment env;
	
    @Bean 
    public SecurityManager securityManager() {
        return new SecurityManager();
    }
    
}
