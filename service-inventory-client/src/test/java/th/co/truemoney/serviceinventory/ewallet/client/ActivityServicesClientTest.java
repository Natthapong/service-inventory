package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public class ActivityServicesClientTest {
	
	private ActivityServicesClient activityServicesClient;
	
	private RestTemplate restTemplate;
	
	private EndPoints endPoints;
	
	private HttpHeaders headers;
	
	@Before
	public void setup(){
		restTemplate = mock(RestTemplate.class);
		
		activityServicesClient = new ActivityServicesClient();
		endPoints = new EndPoints();
		activityServicesClient.setEndPoints(endPoints);
		
		headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		activityServicesClient.setRestTemplate(restTemplate);
		
	}
	
	@SuppressWarnings("rawtypes")
	@Test 
	public void getActivitiesURL(){

		Activity[] activities = new Activity[1];
		
		ResponseEntity<Activity[]> responseEntity = new ResponseEntity<Activity[]>(activities,HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getActivitiesListURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(Activity[].class) , anyString()) ).thenReturn(responseEntity);
		
		activityServicesClient.getActivities("12345");
		
		verify(restTemplate).exchange(eq(endPoints.getActivitiesListURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(Activity[].class) , eq("12345"));
		
	}
	
	@Test
	public void getActivityDetail(){
		
		ActivityDetail activityDetail = new ActivityDetail();
		BigDecimal amount = new BigDecimal(1000);
		activityDetail.setAmount(amount);
		
		ResponseEntity<ActivityDetail> responseEntity = new ResponseEntity<ActivityDetail>(activityDetail,HttpStatus.OK);
		when(restTemplate.exchange(eq(endPoints.getActivityDetailURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(ActivityDetail.class) , anyString() , anyString()) ).thenReturn(responseEntity);
		
		ActivityDetail activityDetailResponse = activityServicesClient.getActivityDetail("1234", "5678");
		assertEquals(amount, activityDetailResponse.getAmount());
		
		verify(restTemplate).exchange(eq(endPoints.getActivityDetailURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(ActivityDetail.class) , anyString() , anyString());
		
	}
}
