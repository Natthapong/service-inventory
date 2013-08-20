package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ForgotPasswordHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ProfileRegisteringHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.TopUpSourceOfFundHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;

@Configuration
public class LegacyFacadeConfig {

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	@Bean
	public EwalletBalanceHandler balanceFacade() {
		return new EwalletBalanceHandler(ewalletProxy);
	}

	@Bean
	public UserProfileHandler profileFacade() {
		return new UserProfileHandler();
	}

	@Bean
	public ProfileRegisteringHandler registeringFacade() {
		return new ProfileRegisteringHandler();
	}

	@Bean
	public TopUpSourceOfFundHandler sourceOfFundFacade() {
		return new TopUpSourceOfFundHandler();
	}

	@Bean
	public LegacyFacade legacyFacade() {
		return new LegacyFacade();
	}

	@Bean
	public BillPaymentHandler billPaymentFacade() {
		return new BillPaymentHandler();
	}
	
	@Bean
	public MobileTopUpHandler topUpMobileFacade() {
		return new MobileTopUpHandler();
	}
	
	@Bean 
	public ForgotPasswordHandler forgotPasswordFacade() {
		return new ForgotPasswordHandler();
	}
	
	@Bean
	public BuyProductHandler buyProductFacade() {
		return new BuyProductHandler();
	}
	
}
