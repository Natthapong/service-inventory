package th.co.truemoney.serviceinventory.bill.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillInvoice;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.bill.impl.BillPaymentServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.TransactionRepository;
import th.co.truemoney.serviceinventory.sms.OTPService;

public class BillPaymentServiceImplTest {

	private BillPaymentServiceImpl billPayService;
	private OTPService otpService;
	private AccessTokenRepository accessTokenRepo;
	private TransactionRepository transactionRepository;

	@Before
	public void setup() {
		billPayService = new BillPaymentServiceImpl();

		otpService = mock(OTPService.class);
		accessTokenRepo = mock(AccessTokenRepository.class);
		transactionRepository = mock(TransactionRepository.class);

		billPayService.setAccessTokenRepo(accessTokenRepo);
		billPayService.setOtpService(otpService);
		billPayService.setTransactionRepository(transactionRepository);

	}

	@Test
	public void createBillInvoice() {

		when(accessTokenRepo.getAccessToken(anyString())).thenReturn(
				new AccessToken("12345", "5555", "4444", "username",
						"0868185055", "tanathip.se@gmail.com", 41));

		when(otpService.send(anyString())).thenReturn(new OTP("0868185055", "12345", "string"));

		when(transactionRepository.getBillInvoice(anyString(), anyString())).thenReturn(new BillInvoice());

		BillInvoice billInvoice = billPayService
				.createBillInvoice(new BillPaymentInfo(), "111111");

		assertNotNull(billInvoice);
	}


}
