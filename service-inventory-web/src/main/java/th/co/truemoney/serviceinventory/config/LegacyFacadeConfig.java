package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileRegisteringFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.SourceOfFundFacade;

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
	
}
