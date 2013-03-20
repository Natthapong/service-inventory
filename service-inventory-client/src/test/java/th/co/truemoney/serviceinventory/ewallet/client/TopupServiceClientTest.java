package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
public class TopupServiceClientTest {
	
	@Autowired
	TopUpServiceClient topupServiceClient;
	
	@Test @Ignore
	public void checkCreateOrderFromDirectDebitUrl(){
		String url = "http://localhost:8585/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
		try{
			RestTemplate restTemplate = mock(RestTemplate.class);
			ResponseEntity<TopUpQuote> responseEntity = new ResponseEntity<TopUpQuote>(new TopUpQuote(), HttpStatus.OK);
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			
			when(
					restTemplate.exchange(eq(url), eq(HttpMethod.POST),
							any(HttpEntity.class), eq(TopUpQuote.class), eq("6789"), eq("12345") , eq(quoteRequest)))
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
	
	@Test 
	public void createOrderFromDirectDebit() {
		try{
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			TopUpQuote topUpQuote = topupServiceClient.createTopUpQuoteFromDirectDebit("678", quoteRequest, "12345");
			
			assertNotNull(topUpQuote);
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
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
	
	@Test @Ignore
	public void requestPlaceOrder() {
		try{
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			TopUpQuote topUpQuote = topupServiceClient.createTopUpQuoteFromDirectDebit("123", quoteRequest, "12345");
			assertNotNull(topUpQuote);
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
}
