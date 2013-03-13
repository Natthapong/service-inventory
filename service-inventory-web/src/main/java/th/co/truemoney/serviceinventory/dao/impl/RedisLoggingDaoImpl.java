package th.co.truemoney.serviceinventory.dao.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;

@Repository
public class RedisLoggingDaoImpl implements RedisLoggingDao {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Override
	public void addData(String key, String value, Long expired) {
		redisTemplate.opsForValue().set(key, value, expired.longValue(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void addData(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);	
	}

	@Override
	public String getData(String key) {
		return redisTemplate.opsForValue().get(key);
	}

}
