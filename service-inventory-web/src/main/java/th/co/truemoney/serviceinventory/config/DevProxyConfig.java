package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.endpoint.EwalletSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.endpoint.TmnProfileSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.endpoint.TmnSecuritySoapEndPointProxy;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.ewallet.proxy")
@Profile("dev")
public class DevProxyConfig {
	
	@Value("${tmnprofile.endpoint}")
	private String tmnProfileSoapEndpoint;
	
	@Value("${tmnsecurity.endpoint}")
	private String tmnSecuritySoapEndpoint;
	
	@Value( "${ewalletsoap.endpoint}")
	private String ewalletSoapEndpoint;
	
	@Bean
	public TmnProfileSoapEndPointProxy tmnProfileSoapEndPointProxy() {
		TmnProfileSoapEndPointProxy endPointProxy = new TmnProfileSoapEndPointProxy(getTrueMoneyProfileSoapEndpoint());
		endPointProxy.setTimeout(5000);
		return endPointProxy;
	}
	
	@Bean
	public TmnSecuritySoapEndPointProxy tmnSecuritySoapEndPointProxy() {
		TmnSecuritySoapEndPointProxy endPointProxy = new TmnSecuritySoapEndPointProxy(getTrueMoneySecuritySoapEndpoint());
		endPointProxy.setTimeout(5000);
		return endPointProxy;
	}
	
	@Bean
	public EwalletSoapEndPointProxy getEwalletSoapEndPointProxy() {
		EwalletSoapEndPointProxy endPointProxy = new EwalletSoapEndPointProxy(getEwalletSoapEndpoint());
		endPointProxy.setTimeout(5000);
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
	
	@Bean @Qualifier("ewalletSoapEndPoint")
	public String getEwalletSoapEndpoint() {
		return ewalletSoapEndpoint;
	}

	@Bean
	public static PropertyPlaceholderConfigurer endPointsProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "endpoints.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}
	
}