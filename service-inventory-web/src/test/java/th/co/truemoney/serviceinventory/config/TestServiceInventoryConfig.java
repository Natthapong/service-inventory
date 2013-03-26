package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.sms.UnSecureOTPGenerator;

@Configuration
public class TestServiceInventoryConfig {

	@Bean @Scope("singleton")
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

	@Bean @Scope("singleton")
	public P2PTransferService mockP2PTransferService() {
		return Mockito.mock(P2PTransferService.class);
	}

    @Bean
    public ExtendAccessTokenAsynService mockExtendAccessTokenAsynService() {
    	return Mockito.mock(ExtendAccessTokenAsynService.class);
    }

    @Bean
	public OTPGenerator otpGenerator() {
		return new UnSecureOTPGenerator();
	}

}
