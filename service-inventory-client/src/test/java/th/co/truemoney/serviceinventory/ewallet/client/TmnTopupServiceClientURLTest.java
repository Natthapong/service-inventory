package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
public class TmnTopupServiceClientURLTest {
	
	@Autowired
	TmnTopUpServiceClient topupServiceClient;
	
	RestTemplate restTemplate;
	
	@Before
	public void setup(){
		restTemplate = mock(RestTemplate.class);
	}
	
	@After
	public void tearDown(){
		reset(restTemplate);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test @Ignore
	public void checkCreateOrderFromDirectDebitUrl(){
		String url = "http://localhost:8585/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
		try{
			
			HashMap hashMap = new HashMap();
			hashMap.put("id", "1234");
			hashMap.put("amount", "2000");
			hashMap.put("username", "username");
			HashMap sourceOfFund = new HashMap();
			sourceOfFund.put("bankCode", "SCB");
			sourceOfFund.put("bankNameEn", "Test");
			sourceOfFund.put("bankNameTh", "Test");
			sourceOfFund.put("bankAccountNumber", "xxx213");
			sourceOfFund.put("minAmount", "20");
			sourceOfFund.put("maxAmount", "30000");
			hashMap.put("sourceOfFund", sourceOfFund);
			hashMap.put("topUpFee", "10");
			hashMap.put("accessTokenID", "99999");
			
			ResponseEntity<HashMap> responseEntity = new ResponseEntity<HashMap>(hashMap, HttpStatus.OK);
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			
			when(
					restTemplate.exchange(eq(url), eq(HttpMethod.POST),
							any(HttpEntity.class), eq(HashMap.class), eq("6789"), eq("12345") , eq(quoteRequest)))
								.thenReturn(responseEntity);
			
			this.topupServiceClient.restTemplate = restTemplate;
			
			TopUpQuote topUpOrder = topupServiceClient.createTopUpQuoteFromDirectDebit("6789", quoteRequest ,"12345");
			assertNotNull(topUpOrder);
			
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
	@Test @Ignore
	public void createOrderFromDirectDebit() {
		try{
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			quoteRequest.setChecksum("");
			TopUpQuote topUpQuote = topupServiceClient.createTopUpQuoteFromDirectDebit("678", quoteRequest, "12345");
			
			assertNotNull(topUpQuote);
		}catch(ServiceInventoryException e){
			assertEquals("404", e.getErrorCode());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
	@Test @Ignore
	public void checkRequestPlaceOrder(){
		try{
			OTP otp = new OTP("112233", "663f78927872f867d883179378a12dde7ae6a71c");			
			TopUpOrder topUpOrder = topupServiceClient.confirmPlaceOrder("1", otp, "12345");
			assertNotNull(topUpOrder);
			System.out.println("finished call remote:" + new Date());
		} catch(ServiceInventoryException e){
			System.out.println(e.getErrorCode());
			assertEquals("1004", e.getErrorCode());
		}
	}
	
	@Test @Ignore
	public void checkRequestPlaceOrderUrl(){
		String url = "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}?accessToken={accessToken}";
		try{
			RestTemplate restTemplate = mock(RestTemplate.class);
			ResponseEntity<TopUpOrder> responseEntity = new ResponseEntity<TopUpOrder>(new TopUpOrder(), HttpStatus.OK);
			
			when(
					restTemplate.exchange(eq(url), eq(HttpMethod.POST),
							any(HttpEntity.class), eq(TopUpOrder.class), eq("12345"), eq("6789"))).thenReturn(responseEntity);
			
			this.topupServiceClient.restTemplate = restTemplate;
			
			TopUpOrder topUpOrder = topupServiceClient.requestPlaceOrder("12345", "6789");
			assertNotNull(topUpOrder);
			
			
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
}
