package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.RandomOTPGeneraor;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.ewallet.proxy")
@Profile("prod")
public class ProdEnvironmentConfig {
	
	@Bean @Qualifier("endpoint.host")
	public String host() {
		return "https://www.truemoney.co.th";
	}

	@Bean
	public OTPGenerator otpGenerator() {
		return new RandomOTPGeneraor();
	}	
	
}