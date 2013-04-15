package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.SmsConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillPayControllerConfirmOTPSuccessTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private BillPaymentService billPaymentServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.billPaymentServiceMock = wac.getBean(BillPaymentService.class);
	}

	@After
	public void tierDown() {
		reset(this.billPaymentServiceMock);
	}

	@Test
	public void confirmOTPSuccess() throws Exception {

		//given
		OTP otp = new OTP("112233", "refCode", "123");

		when(billPaymentServiceMock.confirmBill(anyString(), any(OTP.class), anyString()))
			.thenReturn(BillPaymentDraft.Status.OTP_SENT);

		this.mockMvc.perform(put("/bill-payment/invoice/myInvoiceID/otp/myRefCode?accessTokenID=12345", "1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(otp)))
			.andExpect(status().isOk())
			.andExpect(content().string("\"OTP_SENT\""))
			.andDo(print());
	}

	@Test
	public void confirmOTPFail() throws Exception {

		//given
		OTP otp = new OTP("112233", "refCode", "123");

		when(billPaymentServiceMock.confirmBill(anyString(), any(OTP.class), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.INVALID_OTP, "invalid OTP."));

		this.mockMvc.perform(put("/bill-payment/invoice/myInvoiceID/otp/myRefCode?accessTokenID=12345", "1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(otp)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.INVALID_OTP))
			.andExpect(jsonPath("$.errorDescription").value("invalid OTP."))
			.andDo(print());
	}
}
