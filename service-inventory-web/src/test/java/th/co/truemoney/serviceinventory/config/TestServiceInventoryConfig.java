package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import th.co.truemoney.serviceinventory.ewallet.OTPService;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderRedisRepository;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.dao")
public class TestServiceInventoryConfig {
	
	@Bean @Scope("singleton") @Primary
	public TmnProfileService tmnProfileServiceMock() {
		return Mockito.mock(TmnProfileService.class);
	}
	
	@Bean @Scope("singleton")
	public SourceOfFundService getSourceOfFundService() {
		return Mockito.mock(SourceOfFundService.class);
	}
	
	@Bean @Scope("singleton")
	public TopUpService mockTopUpService() {
		return Mockito.mock(TopUpService.class);
	}
	
	@Bean @Scope("singleton")
	public OTPService getOTPService() {
		return Mockito.mock(OTPService.class);
	}
	
    @Bean @Qualifier("accessTokenMemoryRepository")
    public AccessTokenRepository getAccessTokenMemoryRepository() {
        return new AccessTokenMemoryRepository();
    }

    @Bean @Qualifier("accessTokenRedisRepository")
    public AccessTokenRepository getAccessTokenRedisRepository() {
        return new AccessTokenRedisRepository();
    }

    @Bean @Qualifier("otpMemoryRepository")
    public OTPRepository getOTPMemoryRepository() {
    	return new OTPMemoryRepository();
    }
    
    @Bean @Qualifier("otpRedisRepository")
    public OTPRepository getOTPRedisRepository() {
    	return new OTPRedisRepository();
    }
    
    @Bean @Qualifier("orderMemoryRepository")
    public OrderRepository getOrderMemoryRepository() {
    	return new OrderMemoryRepository();
    }
    
    @Bean @Qualifier("orderRedisRepository")
    public OrderRepository getOrderRedisRepository() {
    	return new OrderRedisRepository();
    }
    
}
