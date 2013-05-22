package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configurable
@Profile("prod")
public class ProdEmailConfig extends DevEmailConfig {

	@Bean
	public static PropertyPlaceholderConfigurer emailProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "email_prod.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}

}
