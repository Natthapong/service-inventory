package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.impl.SourceOfFundServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.endpoint.TmnProfileSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.endpoint.TmnSecuritySoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenRedisRepository;

@Configuration
@ComponentScan(basePackages = "th.co.truemoney.serviceinventory")
public class TestServiceConfig {

	@Value( "${tmnprofile.endpoint}")
	private String tmnProfileSoapEndpoint;
	
	@Value( "${tmnsecurity.endpoint}")
	private String tmnSecuritySoapEndpoint;
	
	@Bean @Scope("singleton")
	public TmnProfileService getTmnProfileService() {
		return Mockito.mock(TmnProfileServiceImpl.class);
	}
	
	@Bean @Scope("singleton")
	public SourceOfFundService getSourceOfFundService() {
		return Mockito.mock(SourceOfFundServiceImpl.class);
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
	
	@Bean @Qualifier("accessTokenMemoryRepository")
	public AccessTokenRepository getAccessTokenMemoryRepository() {
		return new AccessTokenMemoryRepository();
	}

	@Bean @Qualifier("accessTokenRedisRepository")
	public AccessTokenRepository getAccessTokenRedisRepository() {
		return new AccessTokenRedisRepository();
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
