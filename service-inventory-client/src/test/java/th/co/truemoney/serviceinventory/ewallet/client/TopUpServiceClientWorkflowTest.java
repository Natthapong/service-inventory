package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrderStatus;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TopUpServiceClientWorkflowTest {

	@Autowired
	TmnTopUpServiceClient topUpService;

	@Autowired
	TmnProfileServiceClient profileService;

	@Test
	public void shouldSuccessTopUpEwalletUsingDirectDebitWorkflow() throws InterruptedException {

		// login
		String accessToken = profileService.login(41, TestData.createSuccessLogin());
		assertNotNull(accessToken);

		// create quote
		TopUpQuote quote = topUpService.createTopUpQuoteFromDirectDebit("1", new BigDecimal(310), accessToken);

		assertNotNull(quote);
		assertNotNull(quote.getID());

		// get quote details
		quote = topUpService.getTopUpQuoteDetails(quote.getID(), accessToken);

		assertNotNull(quote);
		assertEquals(DraftTransaction.Status.CREATED, quote.getStatus());

		// request otp
		OTP otp = topUpService.sendOTPConfirm(quote.getID(), accessToken);

		assertNotNull(otp);
		assertNotNull(otp.getReferenceCode());

		quote = topUpService.getTopUpQuoteDetails(quote.getID(), accessToken);

		// quote status changed
		assertEquals(DraftTransaction.Status.OTP_SENT, quote.getStatus());

		// confirm otp
		otp.setOtpString("111111");
		DraftTransaction.Status quoteStatus = topUpService.confirmOTP(quote.getID(), otp, accessToken);

		assertNotNull(quoteStatus);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, quoteStatus);

		quote = topUpService.getTopUpQuoteDetails(quote.getID(), accessToken);

		// quote status changed
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, quote.getStatus());

		// get order status
		Thread.sleep(100);
		TopUpOrderStatus topUpOrderStatus = topUpService.getTopUpProcessingStatus(quote.getID(), accessToken);
		assertNotNull(topUpOrderStatus);

		// retry while processing
		while (topUpOrderStatus == TopUpOrderStatus.PROCESSING) {
			topUpOrderStatus = topUpService.getTopUpProcessingStatus(quote.getID(), accessToken);
			System.out.println("processing top up ...");
			Thread.sleep(1000);
		}

		// retry until success
		assertEquals(TopUpOrderStatus.SUCCESS, topUpOrderStatus);

		TopUpOrder topUpOrder = topUpService.getTopUpOrderResults(quote.getID(), accessToken);

		assertNotNull(topUpOrder);
		assertNotNull(topUpOrder.getQuote());
		assertNotNull(topUpOrder.getConfirmationInfo());
		assertEquals(TopUpOrderStatus.SUCCESS, topUpOrder.getStatus());
	}

	@Test
	public void shouldFailToCreateQuoteBecauseOfAmountLessThanBankMinimum() {

			String accessToken = profileService.login(41, TestData.createSuccessLogin());
			assertNotNull(accessToken);

			// min == 300
			try {
				TopUpQuote quote = topUpService.createTopUpQuoteFromDirectDebit("1", new BigDecimal(10), accessToken);
				Assert.fail("should fail because user can not top up under 300 baht");
			} catch (ServiceInventoryException ex) {
				Assert.assertEquals("20001", ex.getErrorCode());
				Assert.assertEquals("amount less than min amount.", ex.getErrorDescription());
				Assert.assertEquals(300.0d, ex.getData().get("minAmount"));
			}
	}

	@Test
	public void shouldFailToCreateQuoteBecauseOfAmountMoreThanBankMaximum() {

			String accessToken = profileService.login(41, TestData.createSuccessLogin());
			assertNotNull(accessToken);

			// max == 3,000
			try {
				TopUpQuote quote = topUpService.createTopUpQuoteFromDirectDebit("1", new BigDecimal(3010), accessToken);
				Assert.fail("should fail because user can not top up over 3000 baht");
			} catch (ServiceInventoryException ex) {
				Assert.assertEquals("20002", ex.getErrorCode());
				Assert.assertEquals("amount more than max amount.", ex.getErrorDescription());
				Assert.assertEquals(3000.0d, ex.getData().get("maxAmount"));
			}
	}


}
