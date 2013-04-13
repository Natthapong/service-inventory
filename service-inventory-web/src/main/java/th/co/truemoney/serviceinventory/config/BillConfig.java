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
@ComponentScan("th.co.truemoney.serviceinventory.bill.proxy")
public class BillConfig {
	
	@Value("${barcode_info.url}")
	private String barcodeInfoURL;
	
	@Value("${confirm_billpay.url}")
	private String confirmBillPayURL;
	
	@Bean @Qualifier("barcodeInfoURL")
	public String barcodeInfoURL() {
		return barcodeInfoURL;
	}	
	@Bean @Qualifier("confirmBillPayURL")
	public String confirmBillPayURL() {
		return confirmBillPayURL;
	}
			
	@Bean
	public static PropertyPlaceholderConfigurer billProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "bill_endpoint.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}

}