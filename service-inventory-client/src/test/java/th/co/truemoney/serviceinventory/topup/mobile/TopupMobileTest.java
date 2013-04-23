package th.co.truemoney.serviceinventory.topup.mobile;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.TopupMobileServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;


public class TopupMobileTest {
	
	TopupMobileServicesClient topupMobileServicesClient;
	
	RestTemplate restTemplate;
	
	EndPoints endPoints;
	
	HttpHeaders headers;
	
	@Before
	public void setup(){
		topupMobileServicesClient = new TopupMobileServicesClient();
		endPoints = new EndPoints();
		topupMobileServicesClient.setEndPoints(endPoints);
		
		restTemplate = mock(RestTemplate.class);
		
		headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void CheckVerifyURL(){
		HttpEntity<HashMap> requestEntity = new HttpEntity<HashMap>(headers);

		this.topupMobileServicesClient.setRestTemplate(restTemplate);
		
		ResponseEntity<HashMap> responseEntity = new ResponseEntity<HashMap>(new HashMap(), HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getVerifyTopupMobile()), eq(HttpMethod.POST), any(ResponseEntity.class)
			, eq(HashMap.class) , eq("12345")) ).thenReturn(responseEntity);
		
		HashMap hashMap = topupMobileServicesClient.verify("12345", "", new BigDecimal(0));
		
		verify(restTemplate).exchange(eq(endPoints.getVerifyTopupMobile()), eq(HttpMethod.POST), any(ResponseEntity.class)
				, eq(HashMap.class) , eq("12345"));
	}
	
}
