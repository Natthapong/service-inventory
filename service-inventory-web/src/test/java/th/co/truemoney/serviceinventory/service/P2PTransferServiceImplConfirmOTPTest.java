package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.EnvConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
import th.co.truemoney.serviceinventory.stub.P2PTransferStubbed;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, EnvConfig.class })
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
        private AsyncP2PTransferProcessor asyncProcessorMock;

        //setup data
        private AccessToken accessToken;
        private OTP goodOTP;
        private P2PTransferDraft transferDraft;

        @Before
        public void setup() {

            //given
            this.asyncProcessorMock = Mockito.mock(AsyncP2PTransferProcessor.class);

            this.p2pService.setAsyncP2PTransferProcessor(asyncProcessorMock);

            accessToken = new AccessToken("1234567890", "1111111111", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
            accessTokenRepo.save(accessToken);

            goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");
            otpRepo.save(goodOTP);

            transferDraft =  P2PTransferStubbed.createP2PDraft(new BigDecimal(100), "0987654321", "target name", "target filename", accessToken.getAccessTokenID());
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
        public void shouldThrowUnVerifiedExceptionWhenUserSkipsOTPVerify() {

        	try {
        		p2pService.performTransfer(transferDraft.getID(), accessToken.getAccessTokenID());
        		fail();
        	} catch (UnVerifiedOwnerTransactionException ex) {
        		assertEquals(Code.OWNER_UNVERIFIED, ex.getErrorCode());
        	}

            verify(asyncProcessorMock, Mockito.never()).transferEwallet(any(P2PTransferTransaction.class), any(AccessToken.class));

        }
}
