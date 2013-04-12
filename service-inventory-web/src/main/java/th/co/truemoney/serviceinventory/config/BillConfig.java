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
	
	@Value("${bill.app_user}")
	private String appUser;
	
	@Value("${bill.app_password}")
	private String appPassword;
	
	@Value("${barcode_info.command}")
	private String barcodeInfoCommand;
	
	@Value("${barcode_info.function_id}")
	private String barcodeInfoFunctionID;
	
	@Value("${barcode_info.service_no}")
	private String barcodeInfoServiceNumber;
	
	@Value("${barcode_info.command_action}")
	private String barcodeInfoCommandAction;
	
	@Value("${barcode_info.url}")
	private String barcodeInfoURL;
	
	@Value("${confirm_billpay.function_id}")
	private String confirmBillPayFunctionID;
	
	@Value("${confirm_billpay.url}")
	private String confirmBillPayURL;
	
	@Bean @Qualifier("appUser")
	public String appUser() {
		return appUser;
	}
	
	@Bean @Qualifier("appPassword")
	public String appPassword() {
		return appPassword;
	}
	
	@Bean @Qualifier("barcodeInfoCommand")
	public String barcodeInfoCommand() {
		return barcodeInfoCommand;
	}
	
	@Bean @Qualifier("barcodeInfoFunctionID")
	public String barcodeInfoFunctionID() {
		return barcodeInfoFunctionID;
	}

	
	@Bean @Qualifier("confirmBillPayFunctionID")
	public String confirmBillPayFunctionID() {
		return confirmBillPayFunctionID;
	}	

	@Bean @Qualifier("barcodeInfoServiceNumber")
	public String barcodeInfoServiceNumber() {
		return barcodeInfoServiceNumber;
	}
	
	@Bean @Qualifier("barcodeInfoCommandAction")
	public String barcodeInfoCommandAction() {
		return barcodeInfoCommandAction;
	}
	
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
	    { new ClassPathResource( "bill.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}

}