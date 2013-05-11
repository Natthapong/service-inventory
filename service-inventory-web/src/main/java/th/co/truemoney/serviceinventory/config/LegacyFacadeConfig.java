package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.legacyfacade.facade.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.BillPaymentFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.ProfileFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.ProfileRegisteringFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.SourceOfFundFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.TopUpMobileFacade;

@Configuration
public class LegacyFacadeConfig {

	@Autowired
	EwalletSoapProxy ewalletProxy;

	@Bean
	public BalanceFacade balanceFacade() {
		return new BalanceFacade(ewalletProxy);
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

	@Bean
	public LegacyFacade legacyFacade() {
		return new LegacyFacade();
	}

	@Bean
	public BillPaymentFacade billPaymentFacade() {
		return new BillPaymentFacade();
	}
	
	@Bean
	public TopUpMobileFacade topUpMobileFacade() {
		return new TopUpMobileFacade();
	}
	
}
