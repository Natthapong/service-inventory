package th.co.truemoney.serviceinventory.ewallet.client.workflows;

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

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.ewallet.client.TmnBillPaymentServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.TransactionAuthenServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class TmnBillPaymentServiceClient_KeyinWorkflowTest {

    @Autowired
    private TmnBillPaymentServiceClient billPaymentServiceClient;

    @Autowired
    private TransactionAuthenServiceClient authenClient;

    @Autowired
    private TmnProfileServiceClient profileService;

    @Test
    public void retrieveBillInformationWithKeyinSuccessCase() throws InterruptedException{

        String accessToken = profileService.login(
                TestData.createAdamSuccessLogin(),
                TestData.createSuccessClientLogin());

        assertNotNull(accessToken);

        Bill bill = billPaymentServiceClient.retrieveBillInformationWithKeyin(
                "aeon", "ref1", "",
                BigDecimal.TEN, InquiryOutstandingBillType.OFFLINE, accessToken);

        assertNotNull(bill);
        assertNotNull(bill.getID());

        BigDecimal amount = new BigDecimal(50);
        BillPaymentDraft billDraft = billPaymentServiceClient.verifyPaymentAbility(bill.getID(), amount, accessToken);
        assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());

        // get transfer draft
        billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
        assertEquals(BillPaymentDraft.Status.CREATED, billDraft.getStatus());


        // send otp and waiting confirm
        OTP otp = authenClient.requestOTP(billDraft.getID(), accessToken);
        assertNotNull(otp);
        assertNotNull(otp.getReferenceCode());

        // get transfer draft and check draft status
        billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
        assertEquals(BillPaymentDraft.Status.OTP_SENT, billDraft.getStatus());

        // confirm otp
        otp.setOtpString("111111");
        BillPaymentDraft.Status draftStatus = authenClient.verifyOTP(billDraft.getID(), otp, accessToken);
        assertNotNull(draftStatus);
        assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, draftStatus);
        assertNotNull(billDraft.getTransactionID());

        // get transfer draft and check draft status
        billDraft = billPaymentServiceClient.getBillPaymentDraftDetail(billDraft.getID(), accessToken);
        assertEquals(BillPaymentDraft.Status.OTP_CONFIRMED, billDraft.getStatus());

        BillPaymentTransaction.Status transactionStatus = billPaymentServiceClient.performPayment(billDraft.getID(), accessToken);
        assertEquals(BillPaymentTransaction.Status.VERIFIED, transactionStatus);
        // get order status
        Thread.sleep(100);
        transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
        assertNotNull(transactionStatus);

        // retry while processing
        while (transactionStatus == BillPaymentTransaction.Status.PROCESSING) {
            transactionStatus = billPaymentServiceClient.getBillPaymentStatus(billDraft.getID(), accessToken);
            Thread.sleep(1000);
        }

        // retry until success
        assertEquals(BillPaymentTransaction.Status.SUCCESS, transactionStatus);

        BillPaymentTransaction transaction = billPaymentServiceClient.getBillPaymentResult(billDraft.getID(), accessToken);

        assertNotNull(transaction);
        assertNotNull(transaction.getDraftTransaction());
        assertNotNull(transaction.getConfirmationInfo());
        assertEquals(BillPaymentTransaction.Status.SUCCESS, transaction.getStatus());
    }

}
