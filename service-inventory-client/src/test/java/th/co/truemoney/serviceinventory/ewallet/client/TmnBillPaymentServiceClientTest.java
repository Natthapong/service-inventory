package th.co.truemoney.serviceinventory.ewallet.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

public class TmnBillPaymentServiceClientTest {
	
	private RestTemplate restTemplate;
	
	private EndPoints endPoints;
	
	private HttpHeaders headers;
	
	private TmnBillPaymentServiceClient tmnBillPaymentServiceClient;
	
	@Before
	public void setup(){
		restTemplate = mock(RestTemplate.class);
		
		tmnBillPaymentServiceClient = new TmnBillPaymentServiceClient();
		endPoints = new EndPoints();
		tmnBillPaymentServiceClient.setEndPoints(endPoints);
		
		headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		tmnBillPaymentServiceClient.setRestTemplate(restTemplate);
		
	}
	
	@Test 
	public void retrieveBillInformationWithKeyin() {

		Bill bill = new Bill();
		
		ResponseEntity<Bill> responseEntity = new ResponseEntity<Bill>(bill,HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getBillInformationServiceWithBillCodeURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(Bill.class), anyString(), anyString()) ).thenReturn(responseEntity);
		
		tmnBillPaymentServiceClient.retrieveBillInformationWithKeyin("1", "accessToken");
		
		verify(restTemplate).exchange(eq(endPoints.getBillInformationServiceWithBillCodeURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(Bill.class) , eq("1"), eq("accessToken"));
		
	}

}
