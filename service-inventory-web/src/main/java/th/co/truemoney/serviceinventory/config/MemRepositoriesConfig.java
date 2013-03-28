package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileMemoryRepository;

@Configuration
@Profile("mem")
public class MemRepositoriesConfig {

    @Bean
    public AccessTokenRepository memAccessTokenRepository() {
    	AccessTokenRepository accessTokenRepository = new AccessTokenMemoryRepository();
    	accessTokenRepository.save(new AccessToken("12345", "666", "888", "username", "0868185055", "tanathip.se@gmail.com",41));
    	return accessTokenRepository;
    }

    @Bean
    public TransactionRepository memOrderRepository() {
    	return new TransactionMemoryRepository();
    }

    @Bean
    public OTPRepository memOTPRepository() {
    	OTPRepository otpRepository = new OTPMemoryRepository();
    	otpRepository.saveOTP(new OTP("0868185055","1111"));
    	return otpRepository;
    }
    
    @Bean
	public ProfileRepository memoProfileRepository() {
		return new ProfileMemoryRepository();
	}
	
}
