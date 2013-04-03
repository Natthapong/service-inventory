package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileRegisteringFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.SourceOfFundFacade;

@Configuration
public class LegacyFacadeConfig {

	@Bean
	public BalanceFacade balanceFacade() {
		return new BalanceFacade();
	}

	@Bean
	public BalanceFacade.TopUpBuilder topUpFacadeBuilder() {
		return new BalanceFacade.TopUpBuilder(balanceFacade());
	}

	@Bean
	public ProfileFacade profileFacade() {
		return new ProfileFacade();
	}

	@Bean
	public ProfileRegisteringFacade registeringFacade() {
		return new ProfileRegisteringFacade();
	}

	@Bean
	public SourceOfFundFacade sourceOfFundFacade() {
		return new SourceOfFundFacade();
	}

}
