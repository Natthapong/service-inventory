package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncP2PTransferProcessor;
import th.co.truemoney.serviceinventory.ewallet.impl.P2PTransferServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.AccessTokenRepositoryStubbed;
import th.co.truemoney.serviceinventory.stub.P2PTransferStubbed;

@RunWith(MockitoJUnitRunner.class)
public class P2PTransferServiceImplTest {

	private P2PTransferServiceImpl p2pService;
	private EwalletSoapProxy ewalletSoapProxyMock;
	private AccessTokenRepository accessTokenRepoMock;
	private TransactionRepository transactionRepo;
	private OTPService otpService;
	private AsyncP2PTransferProcessor asyncP2PTransferProcessor;

	private AccessToken accessToken;
	private P2PTransaction p2pTransaction;

	@Before
	public void setup() {
		this.p2pTransaction = new P2PTransaction();
		this.p2pService = new P2PTransferServiceImpl();
		this.ewalletSoapProxyMock = Mockito.mock(EwalletSoapProxy.class);
		this.accessTokenRepoMock = Mockito.mock(AccessTokenRepository.class);
		this.transactionRepo = Mockito.mock(TransactionRepository.class);
		this.otpService = Mockito.mock(OTPService.class);
		this.asyncP2PTransferProcessor = Mockito.mock(AsyncP2PTransferProcessor.class);

		this.p2pService.setEwalletProxy(this.ewalletSoapProxyMock);
		this.p2pService.setAccessTokenRepository(this.accessTokenRepoMock);
		this.p2pService.setTransactionRepository(this.transactionRepo);
		this.p2pService.setOtpService(otpService);
		this.p2pService.setAsyncP2PTransferProcessor(asyncP2PTransferProcessor);


		accessToken = AccessTokenRepositoryStubbed.createSuccessAccessToken();
		when(accessTokenRepoMock.getAccessToken(Mockito.anyString()))
		.thenReturn(accessToken);
		when(transactionRepo.getP2PDraftTransaction(Mockito.anyString(), Mockito.anyString()))
		.thenReturn(new P2PDraftTransaction());
		when(transactionRepo.getP2PTransaction(Mockito.anyString(), Mockito.anyString()))
		.thenReturn(p2pTransaction);
	}

	@After
	public void teardown() {
		reset(ewalletSoapProxyMock);
		reset(accessTokenRepoMock);
		reset(transactionRepo);
		reset(otpService);
		reset(asyncP2PTransferProcessor);
	}

	@Test
	public void createDraftTransactionSuccess() {
		BigDecimal amount = new BigDecimal(200);
		String mobileNumber = "0811111111";

		//given
		VerifyTransferResponse stubbedVerifyTransferResponse = P2PTransferStubbed.createSuccessStubbedVerifyTransferResponse();

		//when

		when(ewalletSoapProxyMock.verifyTransfer(Mockito.any(VerifyTransferRequest.class)))
			.thenReturn(stubbedVerifyTransferResponse);

		P2PDraftTransaction draftTrans = this.p2pService.createDraftTransaction(mobileNumber, amount, accessToken.getAccessTokenID());

		//then
		assertNotNull(draftTrans);
		assertNotNull(draftTrans.getFullname());
	}

	@Test
	public void getDraftTransactionDetails() {
		P2PDraftTransaction p2pDraftTransaction = this.p2pService.getDraftTransactionDetails("draftTransaction", accessToken.getAccessTokenID());

		assertNotNull(p2pDraftTransaction);
	}

	@Test
	public void sendOTP() {
		OTP mockOTP = new OTP(accessToken.getMobileNumber(), "referenceCode", "otpString");
		when(otpService.send(eq(accessToken.getMobileNumber()))).thenReturn(mockOTP);
		OTP otp = this.p2pService.sendOTP("draftTransactionID", accessToken.getAccessTokenID());

		verify(transactionRepo).saveP2PDraftTransaction(Mockito.any(P2PDraftTransaction.class), Mockito.anyString());
		assertNotNull(otp);
	}

	@Test
	public void confirmDraftTransaction() {
		OTP mockOTP = new OTP(accessToken.getMobileNumber(), "referenceCode", "otpString");

		DraftTransaction.Status status = this.p2pService.confirmDraftTransaction("draftTransactionID", mockOTP, accessToken.getAccessTokenID());

		verify(asyncP2PTransferProcessor).transferEwallet(any(P2PTransaction.class), any(AccessToken.class));
		assertEquals(DraftTransaction.Status.OTP_CONFIRMED, status);
	}

	@Test
	public void getTransactionStatus() {
		p2pTransaction.setStatus(Transaction.Status.VERIFIED);
		Transaction.Status status =  this.p2pService.getTransactionStatus("transactionID", accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.VERIFIED, status);

		p2pTransaction.setStatus(Transaction.Status.PROCESSING);
		status =  this.p2pService.getTransactionStatus("transactionID", accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.PROCESSING, status);

		p2pTransaction.setStatus(Transaction.Status.SUCCESS);
		status =  this.p2pService.getTransactionStatus("transactionID", accessToken.getAccessTokenID());
		assertEquals(Transaction.Status.SUCCESS, status);
	}
}
