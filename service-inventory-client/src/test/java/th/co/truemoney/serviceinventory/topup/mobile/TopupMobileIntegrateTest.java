package th.co.truemoney.serviceinventory.topup.mobile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TopupMobileServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TopupMobileIntegrateTest {

	@Autowired
	private TmnProfileServiceClient profileService;

	@Autowired
	public TopupMobileServicesClient client;

	@Autowired
	public TransactionAuthenServiceClient authenClient;

	@Test
	public void successTopUpMobile() throws InterruptedException{

		String accessToken = profileService.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		TopUpMobileDraft topUpMobileDraft = client.verifyAndCreateTopUpMobileDraft("0868185055", new BigDecimal(500), accessToken);
		assertNotNull(topUpMobileDraft);

		OTP otp = authenClient.requestOTP(topUpMobileDraft.getID() , accessToken);
		assertNotNull(otp);

		otp.setOtpString("111111");
		DraftTransaction.Status transactionStatus = authenClient.verifyOTP(topUpMobileDraft.getID(), otp, accessToken);
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, transactionStatus);

		client.performTopUpMobile(topUpMobileDraft.getID(), accessToken);

		Thread.sleep(100);
		TopUpMobileTransaction.Status status = client.getTopUpMobileStatus(topUpMobileDraft.getID(), accessToken);
		assertNotNull(status);

		while (status == TopUpMobileTransaction.Status.PROCESSING) {
			status = client.getTopUpMobileStatus(topUpMobileDraft.getID(), accessToken);
			System.out.println("processing top up ...");
			Thread.sleep(1000);
		}

		assertEquals(TopUpMobileTransaction.Status.SUCCESS, status);

		TopUpMobileTransaction topUpMobileTransaction = client.getTopUpMobileResult(topUpMobileDraft.getID(), accessToken);
		assertNotNull(topUpMobileTransaction);
		assertNotNull(topUpMobileTransaction.getConfirmationInfo().getTransactionID());
		assertNotNull(topUpMobileTransaction.getConfirmationInfo().getTransactionDate());

	}

}
