package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.P2PTransferStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class P2PTransferServiceImplConfirmOTPTest {

    //unit under test
        @Autowired
        private P2PTransferServiceImpl p2pService;

        @Autowired
        private AccessTokenMemoryRepository accessTokenRepo;

        @Autowired
        private TransactionRepositoryImpl transactionRepo;

        @Autowired
        private OTPMemoryRepository otpRepo;

        //mock
        private OTPService otpServiceMock;
        private AsyncP2PTransferProcessor asyncProcessorMock;

        //setup data
        private AccessToken accessToken;
        private OTP goodOTP;
        private P2PTransferDraft transferDraft;

        @Before
        public void setup() {

            //given
            this.otpServiceMock = Mockito.mock(OTPService.class);
            this.asyncProcessorMock = Mockito.mock(AsyncP2PTransferProcessor.class);

            this.p2pService.setAsyncP2PTransferProcessor(asyncProcessorMock);
            this.p2pService.setOtpService(otpServiceMock);

            accessToken = new AccessToken("1234567890", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
            accessTokenRepo.save(accessToken);

            goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");
            otpRepo.save(goodOTP);

            transferDraft =  P2PTransferStubbed.createP2PDraft(new BigDecimal(100), "0987654321", "target name", accessToken.getAccessTokenID());
            transferDraft.setStatus(P2PTransferDraft.Status.OTP_SENT);

            transactionRepo.setExpirableMap(new MemoryExpirableMap());
            transactionRepo.saveDraftTransaction(transferDraft, accessToken.getAccessTokenID());
        }

        @After
        public void teardown() {
            accessTokenRepo.clear();
            otpRepo.clear();
        }

        @Test
        public void shouldConfirmOTPSuccess() {
            OTP goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");

            P2PTransferDraft.Status quoteStatus = p2pService.verifyOTP(transferDraft.getID(), goodOTP, accessToken.getAccessTokenID());

            assertEquals(P2PTransferDraft.Status.OTP_CONFIRMED, quoteStatus);

            Status performStatus = p2pService.performTransfer(transferDraft.getID(), accessToken.getAccessTokenID());
            assertEquals(P2PTransferTransaction.Status.VERIFIED, performStatus);

            verify(asyncProcessorMock).transferEwallet(any(P2PTransferTransaction.class), any(AccessToken.class));
        }

        @Test
        public void shouldThrowUnVerifiedExceptionWhenUserSkipsOTPVerify() {

        	try {
        		p2pService.performTransfer(transferDraft.getID(), accessToken.getAccessTokenID());
        		fail();
        	} catch (UnVerifiedOwnerTransactionException ex) {
        		assertEquals(Code.OWNER_UNVERIFIED, ex.getErrorCode());
        	}

            verify(asyncProcessorMock, Mockito.never()).transferEwallet(any(P2PTransferTransaction.class), any(AccessToken.class));

        }

        @Test
        public void shouldFailWhenConfirmWithBadAccessToken() {

            OTP invalidOTP = new OTP(accessToken.getMobileNumber(), "refCode", "HACKY");

            try {
                p2pService.verifyOTP(transferDraft.getID(), invalidOTP, "unknown access token");
                Assert.fail();
            } catch (ServiceInventoryException e) {
                assertEquals("10001", e.getErrorCode());
            }

            //should never call the processor
            verify(asyncProcessorMock, Mockito.never()).transferEwallet(any(P2PTransferTransaction.class), any(AccessToken.class));
        }

        @Test
        public void shouldFailWhenConfirmOTPStringIsIncorrect() {

            //when
            Assert.assertEquals(P2PTransferDraft.Status.OTP_SENT, transferDraft.getStatus());
            Mockito.doThrow(new ServiceInventoryWebException("error", "otp error")).when(otpServiceMock).isValidOTP(any(OTP.class));

            try {
                p2pService.verifyOTP(transferDraft.getID(), new OTP(), accessToken.getAccessTokenID());
                Assert.fail();
            } catch (ServiceInventoryException e) {
                Assert.assertEquals("otp error", e.getErrorDescription());
            }

            //then
            Assert.assertEquals(transferDraft.getStatus(), P2PTransferDraft.Status.OTP_SENT);

            try {
                transactionRepo.findTransaction(transferDraft.getID(), accessToken.getAccessTokenID(), P2PTransferTransaction.class);
                Assert.fail("should not create/persist any p2p transaction");
            } catch(ServiceInventoryWebException e) {}

            //should never call the processor
            verify(asyncProcessorMock, Mockito.never()).transferEwallet(any(P2PTransferTransaction.class), any(AccessToken.class));
        }

}
