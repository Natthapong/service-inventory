package th.co.truemoney.serviceinventory.topup.mobile;

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

import th.co.truemoney.serviceinventory.ewallet.client.TopupMobileServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;


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

		this.topupMobileServicesClient.setRestTemplate(restTemplate);
	}
	
	@Test
	public void checkVerifyURL(){

		ResponseEntity<TopUpMobileDraft> responseEntity = new ResponseEntity<TopUpMobileDraft>(new TopUpMobileDraft(), HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getVerifyTopupMobile()), eq(HttpMethod.POST), any(HttpEntity.class)
			, eq(TopUpMobileDraft.class) , eq("12345")) ).thenReturn(responseEntity);
		
		topupMobileServicesClient.verifyAndCreateTopUpMobileDraft("0839952174", new BigDecimal(0) , "12345");
		
		verify(restTemplate).exchange(eq(endPoints.getVerifyTopupMobile()), eq(HttpMethod.POST), any(HttpEntity.class)
				, eq(TopUpMobileDraft.class) , eq("12345"));
	}
	
	@Test(expected=ServiceInventoryException.class)
	public void checkException(){
		
		when(restTemplate.exchange(eq(endPoints.getVerifyTopupMobile()), eq(HttpMethod.POST), any(HttpEntity.class)
			, eq(TopUpMobileDraft.class) , eq("12345")) ).thenThrow(
					new ServiceInventoryException(400,"1001","Invalid Data Exception","SI-ENGINE"));
		
		topupMobileServicesClient.verifyAndCreateTopUpMobileDraft("0839952174", new BigDecimal(0) , "12345");
	}
	
	@Test
	public void checkTopUpMobileSendOTPURL(){
		
		ResponseEntity<OTP> responseEntity = new ResponseEntity<OTP>(HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getSendOTPTopUpMobileURL()), eq(HttpMethod.POST), any(HttpEntity.class)
				, eq(OTP.class) , anyString() ,eq("12345")) ).thenReturn(responseEntity);
		
		topupMobileServicesClient.sendOTP("7788", "12345");
		
		verify(restTemplate).exchange(eq(endPoints.getSendOTPTopUpMobileURL()), eq(HttpMethod.POST), any(HttpEntity.class)
				, eq(OTP.class) , anyString(), eq("12345"));
	}
	
}
