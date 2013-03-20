package th.co.truemoney.serviceinventory.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import th.co.truemoney.serviceinventory.ewallet.OTPService;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.OTPServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.DirectDebitConfig;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.SourceOfFundRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.DirectDebitConfigImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;

@Configuration
@EnableAsync
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

	@Bean
    public OTPService getOtpService() {
    	return new OTPServiceImpl();
    }
	
	@Bean
    public AsyncService getAsyncService() {
    	return new AsyncService();
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

    @Bean @Qualifier("otpMemoryRepository")
    public OTPRepository getOTPMemoryRepository() {
    	return new OTPMemoryRepository();
    }
    
    @Bean 
    public AsyncService getAsyncService() {
    	return new AsyncService();
    }
    
    @Bean 
    public Executor getAsyncExecutor() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(7);
    	executor.setMaxPoolSize(56);
    	executor.setQueueCapacity(11);
    	executor.setThreadNamePrefix("asyncExecutor-");
    	executor.initialize();
    	return executor;
    }
}
