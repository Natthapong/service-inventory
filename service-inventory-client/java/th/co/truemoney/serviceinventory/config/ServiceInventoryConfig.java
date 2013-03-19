package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;

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
    public TopUpService getTopUpService() {
    	return new TopUpServiceImpl();
    }

    @Bean @Qualifier("accessTokenMemoryRepository")
    public AccessTokenRepository getAccessTokenMemoryRepository() {
    	AccessTokenMemoryRepository accessTokenMemoryRepository = new AccessTokenMemoryRepository();
    	accessTokenMemoryRepository.save(new AccessToken("12345", "6789", "555", "username", "0861234567", 41));
        return accessTokenMemoryRepository;
    }

    @Bean @Qualifier("accessTokenRedisRepository")
    public AccessTokenRepository getAccessTokenRedisRepository() {
        return new AccessTokenRedisRepository();
    }
    
    @Bean @Qualifier("orderMemoryRepository")
    public OrderRepository getOrderMemoryRepository() {
    	return new OrderMemoryRepository();
    }
    
    @Bean
    public SourceOfFundRepository sourceOfFundRepo() {
    	return new SourceOfFundRepository();
    }
    
    @Bean 
    public DirectDebitConfig getDirectDebitConfig() {
    	return new DirectDebitConfigImpl();
    }

}
