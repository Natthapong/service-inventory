package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class TestTmnProfileConfig {
	
	@Value("${tmnprofile.initiator}")
	private String tmnProfileInitiator;
	
	@Value("${tmnprofile.pin}")
	private String tmnProfilePin;
	
	@Bean @Qualifier("tmnProfileInitiator")
	public String getTmnProfileInitiator() {
		return tmnProfileInitiator;
	}
	
	@Bean @Qualifier("tmnProfilePin")
	public String getTmnProfilePin() {
		return tmnProfilePin;
	}

	@Bean
	public static PropertyPlaceholderConfigurer tmnProfileProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "tmnprofile.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}
	
}
