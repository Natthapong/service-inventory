package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.firsthop.proxy")
public class TestSmsConfig {
	
	@Value("${sms.shortcode}")
	private String smsShortcode;
	
	@Value("${sms.sender}")
	private String smsSender;
		
	@Value("${firsthop.url}")
	private String firsthopURL;
	
	@Value("${firsthop.username}")
	private String firsthopUser;
	
	@Value("${firsthop.password}")
	private String firsthopPassword;
	
	@Value("${mobile.prefix}")
	private String mobilePrefix;
	
	@Value("${connection.timeout}")
	private Integer connectionTimeout;
	
	@Value("${connection.readTimeout}")
	private Integer connectionReadTimeout;
	
	@Bean @Qualifier("smsShortcode")
	public String getSmsShortcode() {
		return smsShortcode;
	}
	
	@Bean @Qualifier("smsSender")
	public String getSmsSender() {
		return smsSender;
	}

	@Bean @Qualifier("firsthopURL")
	public String getFirsthopURL() {
		return firsthopURL;
	}

	@Bean @Qualifier("firsthopUser")
	public String getFirsthopUser() {
		return firsthopUser;
	}

	@Bean @Qualifier("firsthopPassword")
	public String getFirsthopPassword() {
		return firsthopPassword;
	}

	@Bean @Qualifier("mobilePrefix")
	public String getMobilePrefix() {
		return mobilePrefix;
	}

	@Bean @Qualifier("connectionReadTimeout")
	public Integer getConnectionReadTimeout() {
		return connectionReadTimeout;
	}
	
	@Bean @Qualifier("connectionTimeout")
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	@Bean
	public static PropertyPlaceholderConfigurer smsProxyProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "smsproxy.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}

}
