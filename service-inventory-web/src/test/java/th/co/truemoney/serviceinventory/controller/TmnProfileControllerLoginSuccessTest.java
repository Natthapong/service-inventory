package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jackson.map.ObjectMapper;
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

import th.co.truemoney.serviceinventory.config.TestServiceConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, TestServiceConfig.class })
@ActiveProfiles("local")
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
		
		//given
		when(this.tmnProfileServiceMock.login(any(Integer.class), any(Login.class)))
				.thenReturn("8e48e03be057319f40621fe9bcd123f750f6df1d");
		
		ObjectMapper mapper = new ObjectMapper();
		Login login = new Login("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847");
		this.mockMvc.perform(post("/ewallet/login?channelID=41")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(login)))
			.andExpect(status().isOk())
			.andExpect(content().string("8e48e03be057319f40621fe9bcd123f750f6df1d"))
			.andDo(print());
		
	}
	
}