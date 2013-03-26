package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.dao.impl.RedisLoggingDaoImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.ProfileRedisRepository;

@Configuration
@Profile("redis")
public class RedisRepositoriesConfig {

	@Bean
	public AccessTokenRepository redisAccessTokenRepository() {
		return new AccessTokenRedisRepository();
	}

	@Bean
	public OrderRepository redisTopUpRepository() {
		return new OrderRedisRepository();
	}

	@Bean
	public OTPRepository redisOTPRepository() {
		return new OTPRedisRepository();
	}

	@Bean
	public ProfileRepository redisProfileRepository() {
		return new ProfileRedisRepository();
	}

	@Bean
	public RedisLoggingDao redisLoggingGateway() {
		return new RedisLoggingDaoImpl();
	}

}
