package th.co.truemoney.serviceinventory.ewallet.client.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
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

	private static final int MAX_CONNECTION = 100;
	private static final int MAX_PER_ROUTE = 5;
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60 * 1000;
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ServiceInventoryExceptionResponseErrorHandler());
		restTemplate.setRequestFactory(requestFactory());
		return restTemplate;
	}
	
	@Bean
	public ClientHttpRequestFactory requestFactory() {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionPoolConnectionManager());
		defaultHttpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,DEFAULT_READ_TIMEOUT_MILLISECONDS);

		return new HttpComponentsClientHttpRequestFactory(defaultHttpClient);
	}

	@Bean
	public ClientConnectionManager connectionPoolConnectionManager() {
		PoolingClientConnectionManager poolConnectionManager = new PoolingClientConnectionManager();
		poolConnectionManager.setMaxTotal(MAX_CONNECTION);
		poolConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
		return poolConnectionManager;
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
	
	@Bean
	public SimpleCacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		List<Cache> caches = new ArrayList<Cache>();
		caches.add(cacheBean().getObject());
		cacheManager.setCaches(caches);
		return cacheManager;
	}

	@Bean
	public ConcurrentMapCacheFactoryBean cacheBean() {
		ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
		cacheFactoryBean.setName("billInfo");
		return cacheFactoryBean;
	}

	
}
