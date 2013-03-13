package th.co.truemoney.serviceinventory.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryExceptionResponseErrorHandler;


@Configuration
@ComponentScan(basePackages="th.co.truemoney.serviceinventory.ewallet.client")
public class TestServiceConfig {
	
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ServiceInventoryExceptionResponseErrorHandler());
		return restTemplate;
	}
	
	@Bean
	public EnvironmentConfig getDefaultEnvironment() {
		return new LocalEnvironmentConfig();
	}
	
	@Bean
	public HttpHeaders getDefaultHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		
		return headers;
	}
}