package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;
import th.co.truemoney.serviceinventory.dao.impl.RedisExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.BillInformationRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ForgotPasswordRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileRedisRepository;

@Configuration
@Profile("redis")
public class RedisRepositoriesConfig {

	@Bean
	public AccessTokenRepository redisAccessTokenRepository() {
		return new AccessTokenRedisRepository();
	}

	@Bean
	public TransactionRepository redisTopUpRepository() {
		return new TransactionRepositoryImpl();
	}

	@Bean
	public OTPRepository redisOTPRepository() {
		return new OTPRedisRepository();
	}

	@Bean
	public RegisteringProfileRepository redisProfileRepository() {
		return new ProfileRedisRepository();
	}

	@Bean
	public BillInformationRepository redisBillInfoRepository() {
		return new BillInformationRedisRepository();
	}

	@Bean
	public ForgotPasswordRepository redisForgotPasswordRepository() {
		return new ForgotPasswordRedisRepository();
	}
	
	@Bean
	public ExpirableMap redisLoggingGateway() {
		return new RedisExpirableMap();
	}

}
