package th.co.truemoney.serviceinventory.dao.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import th.co.truemoney.serviceinventory.dao.RedisLoggingDao;
import th.co.truemoney.serviceinventory.util.BasicEncryptUtil;

public class RedisLoggingDaoImpl implements RedisLoggingDao {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void addData(String key, String value, Long expired) {
        redisTemplate.opsForValue().set(key, BasicEncryptUtil.encrypt(value), expired.longValue(), TimeUnit.MINUTES);
    }

    @Override
    public void addData(String key, String value) {
        redisTemplate.opsForValue().set(key, BasicEncryptUtil.encrypt(value));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String getData(String key) {
        return BasicEncryptUtil.decrypt(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void setExpire(String key, Long expired) {
        redisTemplate.expire(key, expired.longValue(), TimeUnit.MINUTES);
    }

}
