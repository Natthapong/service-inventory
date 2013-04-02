package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpFacade;

@Configuration
public class LegacyFacadeConfig {

	@Bean
	public TopUpFacade topUpFacade() {
		return new TopUpFacade();
	}

	@Bean
	public TopUpFacade.DSLBuilder topUpFacadeBuilder() {
		return new TopUpFacade.DSLBuilder(topUpFacade());
	}

}
