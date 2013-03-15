package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;

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

    @Bean @Qualifier("accessTokenMemoryRepository")
    public AccessTokenRepository getAccessTokenMemoryRepository() {
        return new AccessTokenMemoryRepository();
    }

    @Bean @Qualifier("accessTokenRedisRepository")
    public AccessTokenRepository getAccessTokenRedisRepository() {
        return new AccessTokenRedisRepository();
    }
    
    @Bean 
    public DirectDebitConfig getDirectDebitConfig() {
    	return new DirectDebitConfigImpl();
    }

}
