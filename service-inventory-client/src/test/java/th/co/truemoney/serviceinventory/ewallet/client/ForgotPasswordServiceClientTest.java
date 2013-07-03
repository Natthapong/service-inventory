package th.co.truemoney.serviceinventory.ewallet.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForgotPasswordServiceClientTest {
	
	private RestTemplate restTemplate;
	
	private EndPoints endPoints;
	
	private HttpHeaders headers;
	
	private ForgotPasswordServiceClient client;
	
	@Before
	public void setup() {
		restTemplate = mock(RestTemplate.class);
		client = new ForgotPasswordServiceClient();
		
		endPoints = new EndPoints();
		client.setEndPoints(endPoints);
		
		headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		client.setRestTemplate(restTemplate);
	}
	
	@Test
	public void requestForgotPassword() {
		
		ForgotPassword request = new ForgotPassword();
		
		ResponseEntity<ForgotPassword> responseEntity = new ResponseEntity<ForgotPassword>(request,HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getRequestForgotPasswordURL()), eq(HttpMethod.POST), any(HttpEntity.class)
				, eq(ForgotPassword.class))).thenReturn(responseEntity);
		ForgotPassword forgotPasswordResult = client.requestForgotPassword(request);
		Assert.assertNotNull(forgotPasswordResult);
	}

}
