package th.co.truemoney.serviceinventory.ewallet.client.config;


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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
@Import(ServiceInventoryClientConfig.class)
public class ServiceInventoryClientConfigTest {
	
	@Bean @Primary
	public ClientHttpRequestFactory requestFactory() throws NoSuchAlgorithmException, KeyManagementException {

		// set up a TrustManager that trusts everything
		final TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {
		            public X509Certificate[] getAcceptedIssuers() {
		                    System.out.println("getAcceptedIssuers =============");
		                    return null;
		            }

		            public void checkClientTrusted(X509Certificate[] certs,
		                            String authType) {
		                    System.out.println("checkClientTrusted =============");
		            }

		            public void checkServerTrusted(X509Certificate[] certs,
		                            String authType) {
		                    System.out.println("checkServerTrusted =============");
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
}
