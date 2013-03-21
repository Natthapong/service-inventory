package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class })
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
		when(topupServiceMock.requestPlaceOrder(anyString(), anyString())).thenReturn(new TopUpOrder());

		this.mockMvc.perform(post("/top-up/order/{quoteID}?accessTokenID=e6701de94fdda4347a3d31ec5c892ccadc88b847", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

	}
	
	@Test
	public void confirmPlaceOrderSuccess() throws Exception {
		OTP otp = new OTP("112233", "885bdbcc4186d862a7ed3bae4dd3adb3b7de186a");
		//given
		when(topupServiceMock.confirmPlaceOrder(anyString(), any(OTP.class), anyString())).thenReturn(new TopUpOrder());
		
		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(post("/top-up/order/{topUpOrderID}/confirm?accessTokenID=12345", "1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(otp)))
			.andExpect(status().isOk())
			.andDo(print());

	}

}