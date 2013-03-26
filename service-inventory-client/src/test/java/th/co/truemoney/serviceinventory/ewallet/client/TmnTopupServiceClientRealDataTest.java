package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
public class TmnTopupServiceClientRealDataTest {

	@Autowired
	TmnTopUpServiceClient topupServiceClient;
	
	@Test @Ignore
	public void failCaseMinMax(){
		try{
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(10));
			quoteRequest.setChecksum("");
			topupServiceClient.createTopUpQuoteFromDirectDebit("678", quoteRequest, "12345");
		}catch(ServiceInventoryException e){
			assertNotNull(e.getData());
			assertEquals("20001", e.getErrorCode());
		}
	}
	
	@Test @Ignore
	public void getOrderStatus(){
		TopUpStatus topUpStatus = topupServiceClient.getTopUpOrderStatus("333", "12345");
		assertEquals(TopUpStatus.CONFIRMED, topUpStatus);
	}
	
	@Test
	public void getTopUpOrderDetails(){
		TopUpOrder topUpOrder = topupServiceClient.getTopUpOrderDetails("333", "12345");
		assertEquals(topUpOrder.getUsername(), "username");
	}
	
	@Test @Ignore
	public void createOrderFromDirectDebit() {
		try{
			QuoteRequest quoteRequest = new QuoteRequest();
			quoteRequest.setAmount(new BigDecimal(2000));
			quoteRequest.setChecksum("");
			TopUpQuote topUpQuote = topupServiceClient.createTopUpQuoteFromDirectDebit("678", quoteRequest, "12345");
			
			assertNotNull(topUpQuote);
			assertEquals("username",topUpQuote.getUsername());
			assertEquals("SCB", ((DirectDebit) topUpQuote.getSourceOfFund()).getBankCode());
		}catch(ServiceInventoryException e){
			assertEquals("500", e.getErrorCode());
			assertEquals("INTERNAL_SERVER_ERROR", e.getErrorDescription());
			assertEquals("TMN-SERVICE-INVENTORY", e.getErrorNamespace());
		}
	}
	
	@Test
	public void checkRequestPlaceOrder(){
		try{
			OTP otp = new OTP("112233", "885bdbcc4186d862a7ed3bae4dd3adb3b7de186a");			
			TopUpOrder topUpOrder = topupServiceClient.confirmPlaceOrder("1", otp, "12345");
			assertNotNull(topUpOrder);
			System.out.println("finished call remote:" + new Date());
		} catch(ServiceInventoryException e){
			System.out.println(e.getErrorCode());
			assertEquals("1004", e.getErrorCode());
		}
	}

	@Test @Ignore
	public void requestPlaceOrder() {
		TopUpOrder topUpOrder = topupServiceClient.requestPlaceOrder("123", "12345");
		assertNotNull(topUpOrder);
		assertEquals("2000", topUpOrder.getAmount());
	}
}
