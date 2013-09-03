package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import th.co.truemoney.serviceinventory.bean.LoginRequest;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TmnProfileControllerLoginFailTest {

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
	public void shouldLoginFail() throws Exception {

		when(this.tmnProfileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class))
		).thenThrow(
				new SignonServiceException(
						"1",
						"error description"));

		ObjectMapper mapper = new ObjectMapper();
		EWalletOwnerCredential userLogin = new EWalletOwnerCredential("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847", 40);
		ClientCredential clientLogin = new ClientCredential("appKey", "appUser", "appPassword", "chanel", "channel detail");

		LoginRequest loginRequest = new LoginRequest(userLogin, clientLogin);

		this.mockMvc.perform(post("/ewallet/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(loginRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("1"))
			.andExpect(jsonPath("$.errorDescription").value("error description"))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));

	}

}