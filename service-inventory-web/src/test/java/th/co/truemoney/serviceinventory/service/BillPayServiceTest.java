package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;

public class BillPayServiceTest {

	private BillPaymentServiceImpl billPaymentService;
	private OTPService otpService;
	private AccessTokenRepository accessTokenRepo;
	private TransactionRepository transactionRepository;
	private LegacyFacade legacyFacade;
	private BillPaymentFacade billPaymentFacade;

	@Before
	public void setup() {
		billPaymentService = new BillPaymentServiceImpl();
		legacyFacade = new LegacyFacade();
		
		otpService = mock(OTPService.class);
		accessTokenRepo = mock(AccessTokenRepository.class);
		transactionRepository = mock(TransactionRepository.class);
		billPaymentFacade = mock(BillPaymentFacade.class);
		
		legacyFacade.setBillPaymentFacade(billPaymentFacade);
		
		billPaymentService.setAccessTokenRepo(accessTokenRepo);
		billPaymentService.setOtpService(otpService);
		billPaymentService.setTransactionRepository(transactionRepository);
		billPaymentService.setLegacyFacade(legacyFacade);

	}

	@Test
	public void getBillInformation() {
		
		BillInfo stubbedBillPaymentInfo = BillPaymentStubbed.createSuccessBillPaymentInfo();
		// given 
		when(accessTokenRepo.findAccessToken(anyString())).thenReturn(
				new AccessToken("12345", "5555", "4444", "username",
						"0868185055", "tanathip.se@gmail.com", 41));
		
		when(billPaymentFacade.getBarcodeInformation(41, "|010554614953100 010004552 010520120200015601 85950")).thenReturn(stubbedBillPaymentInfo);
		
		//when
		BillInfo billPaymentInfo = billPaymentService.getBillInformation("|010554614953100 010004552 010520120200015601 85950", "12345");
		
		//then
		assertNotNull(billPaymentInfo);
		verify(billPaymentFacade).getBarcodeInformation(anyInt(), anyString());
		
	}
	
	@Test
	public void createBillInvoice() {

		when(accessTokenRepo.findAccessToken(anyString())).thenReturn(
				new AccessToken("12345", "5555", "4444", "username",
						"0868185055", "tanathip.se@gmail.com", 41));
		
		when(otpService.send(anyString())).thenReturn(new OTP("0868185055", "12345", "string"));

		when(transactionRepository.findBill(anyString(), anyString())).thenReturn(new Bill());
		
		Bill billInvoice = billPaymentService.createBill(new BillInfo(), "111111");
		
		assertNotNull(billInvoice);
	}
	
	
}
