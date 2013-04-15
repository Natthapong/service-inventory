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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnTopupServiceClientURLTest {

	@Autowired
	TmnTopUpServiceClient topupServiceClient;

	@Autowired
	TmnProfileServiceClient client;

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

		String accessToken = client.login(41, TestData.createSuccessLogin());
		// create quote
		TopUpQuote quote = topupServiceClient.createAndVerifyTopUpQuote("1", new BigDecimal(310), accessToken);

		assertNotNull(quote);
		assertNotNull(quote.getID());;
	}

	@Test
	public void confirmOTPSuccess(){

		// login
		String accessToken = client.login(41, TestData.createSuccessLogin());
		assertNotNull(accessToken);

		// create quote
		TopUpQuote quote = topupServiceClient.createAndVerifyTopUpQuote("1", new BigDecimal(310), accessToken);

		assertNotNull(quote);
		assertNotNull(quote.getID());

		// get quote details
		quote = topupServiceClient.getTopUpQuoteDetails(quote.getID(), accessToken);

		assertNotNull(quote);
		assertEquals(TopUpQuote.Status.CREATED, quote.getStatus());

		// request otp
		OTP otp = topupServiceClient.submitTopUpRequest(quote.getID(), accessToken);

		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		quote = topupServiceClient.getTopUpQuoteDetails(quote.getID(), accessToken);

		// quote status changed
		assertEquals(TopUpQuote.Status.OTP_SENT, quote.getStatus());

	}

}
