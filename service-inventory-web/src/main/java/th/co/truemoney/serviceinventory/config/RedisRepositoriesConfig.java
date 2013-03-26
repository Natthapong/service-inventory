package th.co.truemoney.serviceinventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.dao.impl.RedisLoggingDaoImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPRedisRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OrderRedisRepository;

@Configuration
@Profile("redis")
public class RedisRepositoriesConfig {

	@Bean
	public AccessTokenRepository getAccessTokenRedisRepository() {
		return new AccessTokenRedisRepository();
	}

	@Bean
	public OrderRepository getOrderRedisRepository() {
		return new OrderRedisRepository();
	}

	@Bean
	public OTPRepository getOTPRedisRepository() {
		return new OTPRedisRepository();
	}

	@Bean
	public RedisLoggingDao redisLoggingGateway() {
		return new RedisLoggingDaoImpl();
	}
}
