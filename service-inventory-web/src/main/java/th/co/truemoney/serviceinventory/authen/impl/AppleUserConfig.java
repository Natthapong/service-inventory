package th.co.truemoney.serviceinventory.authen.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppleUserConfig {

	private static Logger logger = LoggerFactory.getLogger(AppleUserConfig.class);
	
	private Map<String, AppleUser> appleMap;
	
	public AppleUserConfig() {
		JsonFactory factory = new JsonFactory();
		ObjectMapper m = new ObjectMapper(factory);

		TypeReference<HashMap<String, AppleUser>> typeRef;
			typeRef = new TypeReference<HashMap<String, AppleUser>>() {
		};

		ClassPathResource resource = new ClassPathResource("apple/user.json");
		try {
			appleMap = m.readValue(resource.getFile(), typeRef);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public AppleUser getAppleUser(String truemoneyId) {
		return appleMap.get(truemoneyId);
	}
	
}
