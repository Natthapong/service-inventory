package th.co.truemoney.serviceinventory.ewallet.client.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
@ComponentScan(basePackages="th.co.truemoney.serviceinventory.ewallet.client")
public class ServiceInventoryClientConfig {

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ServiceInventoryExceptionResponseErrorHandler());
		restTemplate.setRequestFactory(requestFactory());
		return restTemplate;
	}

	@Bean
	public ClientHttpRequestFactory requestFactory() {
		return new HttpComponentsClientHttpRequestFactory(new DefaultHttpClient());
	}

	@Bean
	public HttpHeaders defaultHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();

		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	@Bean
	public EndPoints endPoints() {
		return new EndPoints();
	}
}
