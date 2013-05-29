package th.co.truemoney.serviceinventory.ewallet.client.config;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.domain.Bill;

@Configuration
@Import(ServiceInventoryClientConfig.class)
public class ServiceInventoryClientConfigTest {
	
	@Bean @Primary
	public ClientHttpRequestFactory requestFactory() throws NoSuchAlgorithmException, KeyManagementException {

		// set up a TrustManager that trusts everything
		final TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {
		            public X509Certificate[] getAcceptedIssuers() {
		                    return null;
		            }

		            public void checkClientTrusted(X509Certificate[] certs,
		                            String authType) {
		            }

		            public void checkServerTrusted(X509Certificate[] certs,
		                            String authType) {
		            }
				}
		};

		final SSLContext sslContext = SSLContext.getInstance( "SSL" );
	    sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
	    final SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme httpsScheme = new Scheme("https", 9443, sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);

		// apache HttpClient version >4.2 should use BasicClientConnectionManager
		ClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);
		return new HttpComponentsClientHttpRequestFactory(new DefaultHttpClient(cm));
	}
	
	@Bean @Primary
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		when(
				restTemplate.exchange(
						anyString(), 
						eq(HttpMethod.GET), 
						any(HttpEntity.class), 
						eq(Bill.class),
						anyString(),
						anyString()) 
				).thenReturn(
						new ResponseEntity<Bill>(
								new Bill("0", "tmvh", "ref1", "ref2", BigDecimal.TEN), 
								HttpStatus.OK)
				);
		return restTemplate;
	}
}
