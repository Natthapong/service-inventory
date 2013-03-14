package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.dao")
public class ServiceInventoryConfig {

	@Bean
	public TmnProfileService getTmnProfileService() {
		return new TmnProfileServiceImpl();
	}

	@Bean
	public SourceOfFundService getSourceOfFundService() {
		return new SourceOfFundServiceImpl();
	}

    @Bean
    public AccessTokenRepository getAccessTokenMemoryRepository() {
        return new AccessTokenMemoryRepository();
    }

}
