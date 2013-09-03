package th.co.truemoney.serviceinventory.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import th.co.truemoney.serviceinventory.authen.impl.AppleUser;
import th.co.truemoney.serviceinventory.authen.impl.AppleUserMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Profile("prod") 
public class ProdAppleUserConfig {
	
	@Bean
	public AppleUserMap appleUsers() {
		AppleUserMap appleUserMap = new AppleUserMap();
		JsonFactory factory = new JsonFactory();
		ObjectMapper m = new ObjectMapper(factory);

		TypeReference<HashMap<String, AppleUser>> typeRef;
			typeRef = new TypeReference<HashMap<String, AppleUser>>() {
		};

		ClassPathResource resource = new ClassPathResource("apple/user_prod.json");
		try {
			Map<String, AppleUser> appleUsers = m.readValue(resource.getFile(), typeRef);
			appleUserMap.setAppleUsers(appleUsers); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appleUserMap;
	}
	
} 
