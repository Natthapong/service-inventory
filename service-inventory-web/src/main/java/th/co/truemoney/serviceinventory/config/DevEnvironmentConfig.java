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
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.endpoint.TmnProfileAdminSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.endpoint.TmnProfileSoapEndPointProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.endpoint.TmnSecuritySoapEndPointProxy;
import th.co.truemoney.serviceinventory.sms.OTPGenerator;
import th.co.truemoney.serviceinventory.sms.RandomOTPGeneraor;

@Configuration
@ComponentScan("th.co.truemoney.serviceinventory.ewallet.proxy")
@Profile("dev")
public class DevEnvironmentConfig {

	@Value("${tmnprofile.endpoint}")
	private String tmnProfileSoapEndpoint;

	@Value("${tmnprofile.timeout}")
	private Integer tmnProfileSoapTimeout;

	@Value("${tmnsecurity.endpoint}")
	private String tmnSecuritySoapEndpoint;

	@Value("${tmnsecurity.timeout}")
	private Integer tmnSecuritySoapTimeout;

	@Value( "${ewalletsoap.endpoint}")
	private String ewalletSoapEndpoint;

	@Value( "${ewalletsoap.timeout}")
	private Integer ewalletSoapTimeout;

	@Value("${tmnprofileadmin.endpoint}")
	private String tmnProfileAdminSoapEndpoint;

	@Value("${tmnprofileadmin.timeout}")
	private Integer tmnProfileAdminSoapTimeout;

	@Bean
	public TmnProfileSoapEndPointProxy tmnProfileSoapEndPointProxy() {
		TmnProfileSoapEndPointProxy endPointProxy = new TmnProfileSoapEndPointProxy(trueMoneyProfileSoapEndpoint());
		endPointProxy.setTimeout(tmnProfileSoapTimeout);
		return endPointProxy;
	}

	@Bean
	public TmnSecuritySoapEndPointProxy tmnSecuritySoapEndPointProxy() {
		TmnSecuritySoapEndPointProxy endPointProxy = new TmnSecuritySoapEndPointProxy(trueMoneySecuritySoapEndpoint());
		endPointProxy.setTimeout(tmnSecuritySoapTimeout);
		return endPointProxy;
	}

	@Bean
	public EwalletSoapEndPointProxy ewalletSoapEndPointProxy() {
		EwalletSoapEndPointProxy endPointProxy = new EwalletSoapEndPointProxy(ewalletSoapEndpoint());
		endPointProxy.setTimeout(ewalletSoapTimeout);
		return endPointProxy;
	}

	@Bean
	public TmnProfileAdminSoapEndPointProxy tmnProfileAdminSoapEndPointProxy() {
		TmnProfileAdminSoapEndPointProxy endPointProxy = new TmnProfileAdminSoapEndPointProxy(trueMoneyProfileAdminSoapEndpoint());
		endPointProxy.setTimeout(tmnProfileAdminSoapTimeout);
		return endPointProxy;
	}

	@Bean @Qualifier("tmnProfileSoapEndPoint")
	public String trueMoneyProfileSoapEndpoint() {
		return tmnProfileSoapEndpoint;
	}

	@Bean @Qualifier("tmnSecuritySoapEndPoint")
	public String trueMoneySecuritySoapEndpoint() {
		return tmnSecuritySoapEndpoint;
	}

	@Bean @Qualifier("ewalletSoapEndPoint")
	public String ewalletSoapEndpoint() {
		return ewalletSoapEndpoint;
	}

	@Bean @Qualifier("tmnProfileAdminSoapEndPoint")
	public String trueMoneyProfileAdminSoapEndpoint() {
		return tmnProfileAdminSoapEndpoint;
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

	@Bean
	public OTPGenerator otpGenerator() {
		return new RandomOTPGeneraor();
	}
}