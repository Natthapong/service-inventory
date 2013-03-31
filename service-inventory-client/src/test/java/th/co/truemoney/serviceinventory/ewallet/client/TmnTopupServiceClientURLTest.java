package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
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
	@Test 
	public void checkCreateOrderFromDirectDebitUrl(){
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

		
		// create quote
		TopUpQuote quote = topupServiceClient.createTopUpQuoteFromDirectDebit("1", new BigDecimal(310), "12345");

		assertNotNull(quote);
		assertNotNull(quote.getID());;
	}

	@Test 
	public void checkRequestPlaceOrderUrl(){
		// request otp
		OTP otp = topupServiceClient.sendOTPConfirm("xxxx", "12345");

		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		TopUpQuote quote = topupServiceClient.getTopUpQuoteDetails("xxxx", "12345");

		// quote status changed
		assertEquals(DraftTransaction.Status.OTP_SENT, quote.getStatus());
	}

}
