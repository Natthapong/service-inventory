package th.co.truemoney.serviceinventory.dao.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.util.EncryptUtil;

public class RedisLoggingDaoImpl implements RedisLoggingDao {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Override
	public void addData(String key, String value, Long expired) {
		redisTemplate.opsForValue().set(key, EncryptUtil.encrypt(value), expired.longValue(), TimeUnit.MINUTES);
	}

	@Override
	public void addData(String key, String value) {
		redisTemplate.opsForValue().set(key, EncryptUtil.encrypt(value));
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public String getData(String key) {
		return EncryptUtil.decrypt(redisTemplate.opsForValue().get(key));
	}

	@Override
	public void setExpire(String key, Long expired) {
		redisTemplate.expire(key, expired.longValue(), TimeUnit.MINUTES);
	}

}
