package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
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

	@Test @Ignore
	public void requestPlaceOrder() {
		TopUpOrder topUpOrder = topupServiceClient.requestPlaceOrder("123", "12345");
		assertNotNull(topUpOrder);
		assertEquals("2000", topUpOrder.getAmount());
	}
}
