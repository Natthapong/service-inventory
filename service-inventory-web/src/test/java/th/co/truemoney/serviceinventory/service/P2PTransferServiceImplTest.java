package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.OTPMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.P2PTransferStubbed;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction.FailStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class P2PTransferServiceImplTest {

	//unit under test
	@Autowired
	private P2PTransferServiceImpl p2pService;

	@Autowired
	private AccessTokenMemoryRepository accessTokenRepo;

	@Autowired
	private TransactionMemoryRepository transactionRepo;

	@Autowired
	private OTPMemoryRepository otpRepo;

	//mock
	private OTPService otpServiceMock;
	private AsyncP2PTransferProcessor asyncP2PProcessorMock;

	//setup data
	private AccessToken accessToken;
	private OTP goodOTP;
	private P2PDraftTransaction draftTrans;

	@Before
	public void setup() {

		this.otpServiceMock = Mockito.mock(OTPService.class);
		this.asyncP2PProcessorMock = Mockito.mock(AsyncP2PTransferProcessor.class);

		this.p2pService.setOtpService(otpServiceMock);
		this.p2pService.setAsyncP2PTransferProcessor(asyncP2PProcessorMock);

		accessToken = new AccessToken("1234567890", "0987654321", "1111111111", "user1.test.v1@gmail.com", "0866012345", "local@tmn.com", 41);
		accessTokenRepo.save(accessToken);

		goodOTP = new OTP(accessToken.getMobileNumber(), "refCode", "OTPpin");
		otpRepo.saveOTP(goodOTP);

		draftTrans =  P2PTransferStubbed.createP2PDraft(new BigDecimal(100), "0987654321", "target name", accessToken.getAccessTokenID());
		transactionRepo.saveP2PDraftTransaction(draftTrans, accessToken.getAccessTokenID());
	}

	@After
	public void teardown() {
		accessTokenRepo.clear();
		transactionRepo.clear();
		otpRepo.clear();
	}

	@Test
	public void shouldVerifyAndCreateTransferDraftSuccess() {

		//given
		BigDecimal amount = new BigDecimal(200);
		String targetMobile = "0987654321";

		//when
		P2PDraftTransaction draftTrans = this.p2pService.verifyAndCreateTransferDraft(targetMobile, amount, accessToken.getAccessTokenID());

		//then
		assertNotNull(draftTrans);
		assertNotNull(draftTrans.getFullname());

		P2PDraftTransaction newDraftTrans = this.p2pService.getTransferDraftDetails(draftTrans.getID(), accessToken.getAccessTokenID());
		assertNotNull(newDraftTrans);
	}

	@Test
	public void shouldSendConfirmingOTPWhenSubmitTransferral() {

		//given
		OTP mockOTP = new OTP(accessToken.getMobileNumber(), "referenceCode", "otpString");
		when(otpServiceMock.send(accessToken.getMobileNumber())).thenReturn(mockOTP);
		assertEquals(Status.CREATED, draftTrans.getStatus());

		//when
		OTP otp = this.p2pService.submitTransferral(draftTrans.getID(), accessToken.getAccessTokenID());

		//then
		assertNotNull(otp);
		P2PDraftTransaction repoValue = transactionRepo.getP2PDraftTransaction(draftTrans.getID(), accessToken.getAccessTokenID());
		assertEquals(Status.OTP_SENT, repoValue.getStatus());
	}

	@Test
	public void shouldReturnCorrectStatusWhenGetTransactionStatusGivesGoodStatuses() {

		//given
		draftTrans.setStatus(Status.OTP_CONFIRMED);
		P2PTransaction p2pTrans = new P2PTransaction(draftTrans);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//given status is verified
		p2pTrans.setStatus(Transaction.Status.VERIFIED);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when status is verified
		Transaction.Status status =  this.p2pService.getTransferingStatus(draftTrans.getID(), accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.VERIFIED, status);

		//given status is processing
		p2pTrans.setStatus(Transaction.Status.PROCESSING);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when status is processing
		status =  this.p2pService.getTransferingStatus(draftTrans.getID(), accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.PROCESSING, status);

		//given status is success
		p2pTrans.setStatus(Transaction.Status.SUCCESS);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when status is processing
		status =  this.p2pService.getTransferingStatus(draftTrans.getID(), accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.SUCCESS, status);
	}

	@Test
	public void shouldThrowExceptionWhenGetTransactionStatusGivesBadStatuses() {

		//given
		draftTrans.setStatus(Status.OTP_CONFIRMED);
		P2PTransaction p2pTrans = new P2PTransaction(draftTrans);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//given status has failed because umarket
		p2pTrans.setStatus(Transaction.Status.FAILED);
		p2pTrans.setFailStatus(FailStatus.UMARKET_FAILED);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when
		try {
			this.p2pService.getTransferingStatus(draftTrans.getID(), accessToken.getAccessTokenID());
			fail();
		} catch (ServiceInventoryWebException ex) {
			assertEquals(Code.CONFIRM_UMARKET_FAILED, ex.getErrorCode());
		}

		//given status has failed because unknown failure
		p2pTrans.setStatus(Transaction.Status.FAILED);
		p2pTrans.setFailStatus(FailStatus.UNKNOWN_FAILED);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when
		try {
			this.p2pService.getTransferingStatus(draftTrans.getID(), accessToken.getAccessTokenID());
			fail();
		} catch (ServiceInventoryWebException ex) {
			assertEquals(Code.CONFIRM_FAILED, ex.getErrorCode());
		}
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenGetTransactionStatusWithBadKeys() {
		//given
		draftTrans.setStatus(Status.OTP_CONFIRMED);
		P2PTransaction p2pTrans = new P2PTransaction(draftTrans);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//given status has failed because umarket
		p2pTrans.setStatus(Transaction.Status.SUCCESS);
		transactionRepo.saveP2PTransaction(p2pTrans, accessToken.getAccessTokenID());

		//when using bad trans id
		try {
			this.p2pService.getTransferingStatus("bad trans id", accessToken.getAccessTokenID());
			fail();
		} catch (ResourceNotFoundException ex) {
			assertEquals(Code.TRANSACTION_NOT_FOUND, ex.getErrorCode());
		}

		//when using bad access token
		try {
			this.p2pService.getTransferingStatus(p2pTrans.getID(), "bad access token");
			fail();
		} catch (ResourceNotFoundException ex) {
			assertEquals(Code.ACCESS_TOKEN_NOT_FOUND, ex.getErrorCode());
		}
	}
}
