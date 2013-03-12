package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.endpoint.TmnProfileSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.endpoint.TmnSecuritySoapEndPointProxy;

@Configuration
@ComponentScan(basePackages = "th.co.truemoney.serviceinventory")
public class ServiceConfig {
	
	@Value( "${tmnprofile.endpoint}")
	private String tmnProfileSoapEndpoint;
	
	@Value( "${tmnsecurity.endpoint}")
	private String tmnSecuritySoapEndpoint;
	
	@Bean
	public TmnProfileService getTmnProfileService() {
		return new TmnProfileServiceImpl();
	}
	
	@Bean
	public TmnProfileSoapEndPointProxy getTmnProfileSoapEndPointProxy() {
		TmnProfileSoapEndPointProxy endPointProxy = new TmnProfileSoapEndPointProxy(getTrueMoneyProfileSoapEndpoint());
		endPointProxy.setTimeout(10000);
		return endPointProxy;
	}
	
	@Bean
	public TmnSecuritySoapEndPointProxy getTmnSecuritySoapEndPointProxy() {
		TmnSecuritySoapEndPointProxy endPointProxy = new TmnSecuritySoapEndPointProxy(getTrueMoneySecuritySoapEndpoint());
		endPointProxy.setTimeout(10000);
		return endPointProxy;
	}
	
	@Bean @Qualifier("tmnProfileSoapEndPoint")
	public String getTrueMoneyProfileSoapEndpoint() {
		return tmnProfileSoapEndpoint;
	}
	
	@Bean @Qualifier("tmnSecuritySoapEndPoint")
	public String getTrueMoneySecuritySoapEndpoint() {
		return tmnSecuritySoapEndpoint;
	}
	
	@Bean
	public static PropertyPlaceholderConfigurer properties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "application.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}
	
}