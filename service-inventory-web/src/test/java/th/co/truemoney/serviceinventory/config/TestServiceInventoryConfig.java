package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;

@Configuration
public class TestServiceInventoryConfig {
	
	@Bean @Scope("singleton") @Primary
	public TmnProfileService tmnProfileServiceMock() {
		return Mockito.mock(TmnProfileService.class);
	}
	
	@Bean @Scope("singleton")
	public SourceOfFundService getSourceOfFundService() {
		return Mockito.mock(SourceOfFundService.class);
	}
	
	@Bean
	public AccessTokenRepository accessTokenRepo() {
		return new AccessTokenMemoryRepository();
	}

}
