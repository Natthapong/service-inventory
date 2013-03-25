package th.co.truemoney.serviceinventory.dao;

public interface RedisLoggingDao {
	public void addData(String key, String value, Long expired);
	public void addData(String key, String value);
	public void delete(String key);
	public String getData(String key);
	public void setExpire(String key, Long expired);
}
