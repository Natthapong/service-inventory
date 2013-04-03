package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.legacyfacade.ewallet.EwalletFacade;

@Configuration
public class LegacyFacadeConfig {

	@Bean
	public EwalletFacade topUpFacade() {
		return new EwalletFacade();
	}

	@Bean
	public EwalletFacade.TopUpBuilder topUpFacadeBuilder() {
		return new EwalletFacade.TopUpBuilder(topUpFacade());
	}

}
