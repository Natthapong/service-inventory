package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import th.co.truemoney.serviceinventory.bean.LoginRequest;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.SmsConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TmnProfileControllerLoginSuccessTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);
	}

	@After
	public void tierDown() {
		reset(this.tmnProfileServiceMock);
	}

	@Test
	public void shouldLoginSuccess() throws Exception {

		when(this.tmnProfileServiceMock.login(
				any(EWalletOwnerLogin.class),
				any(ClientLogin.class),
				any(ChannelInfo.class)))
				.thenReturn("8e48e03be057319f40621fe9bcd123f750f6df1d");

		ObjectMapper mapper = new ObjectMapper();
		EWalletOwnerLogin userLogin = new EWalletOwnerLogin("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
		ClientLogin clientLogin = new ClientLogin("appKey", "appUser", "appPassword");
		ChannelInfo channelInfo = new ChannelInfo(40, "channel", "detail");

		LoginRequest loginRequest = new LoginRequest(userLogin, clientLogin, channelInfo);


		this.mockMvc.perform(post("/ewallet/login?channelID=41")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("8e48e03be057319f40621fe9bcd123f750f6df1d"))
			.andDo(print());

	}

}