package th.co.truemoney.serviceinventory.dao.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.dao.ExpirableMap;

public class MemoryExpirableMap  implements ExpirableMap {

	private HashMap<String, String> data = new LinkedHashMap<String, String>();

	@Override
	public void addData(String key, String value, Long expired) {
		data.put(key, value);
	}

	@Override
	public void addData(String key, String value) {
		data.put(key, value);
	}

	@Override
	public void delete(String key) {
		data.remove(key);

	}

	@Override
	public String getData(String key) {
		return data.get(key);
	}

	@Override
	public void setExpire(String key, Long expired) {
		// do nothing
	}

}