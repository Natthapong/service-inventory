package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.RandomOTPGeneraor;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.ewallet.proxy")
@Profile("dev")
public class DevEnvironmentConfig {
	
	@Bean @Qualifier("core.report.endpoint.host")
	public String coreReportWebHost() {
		return "https://localhost:9443";
	}
	
    @Bean @Qualifier("endpoint.host") 
    @Primary
    public String coreServiceWebHost() {
        return "http://127.0.0.1:9443";
    }

	@Bean
	public OTPGenerator otpGenerator() {
		return new RandomOTPGeneraor();
	}	
	
}