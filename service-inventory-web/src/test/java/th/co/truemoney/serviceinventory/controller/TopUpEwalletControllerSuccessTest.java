package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.SmsConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TopUpEwalletControllerSuccessTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TopUpService topupServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.topupServiceMock = wac.getBean(TopUpService.class);
	}

	@After
	public void tierDown() {
		reset(this.topupServiceMock);
	}

	@Test
	public void shouldSuccess() throws Exception {

		//given
		when(topupServiceMock.submitTopUpRequest(anyString(), anyString())).thenReturn(new OTP("11233", "refCode"));

		this.mockMvc.perform(post("/top-up/quote/{quoteID}/otp?accessTokenID=e6701de94fdda4347a3d31ec5c892ccadc88b847", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

	}

	@Test
	public void confirmPlaceOrderSuccess() throws Exception {
		OTP otp = new OTP("112233", "refCode", "123");
		//given
		when(topupServiceMock.authorizeAndPerformTopUp(anyString(), any(OTP.class), anyString())).thenReturn(TopUpQuote.Status.OTP_SENT);

		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(put("/top-up/quote/{quoteID}/otp/refCode?accessTokenID=12345", "1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(otp)))
			.andExpect(status().isOk())
			.andDo(print());

	}

}