package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ChangePin;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class ChangePinControllerTest {

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
	public void shouldChangePinSuccess() throws Exception {
		
		String stubbedMobileNumber = "08xxxxxxxx";		
		
		when(this.tmnProfileServiceMock.changePin(anyString(), any(ChangePin.class))).thenReturn(stubbedMobileNumber);
		
		ObjectMapper mapper = new ObjectMapper();
		
		this.mockMvc.perform(put("/ewallet/profile/change-pin?channelID={channelID}&accessTokenID={accessTokenID}", "40", "TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new ChangePin("0000", "1111"))))
				.andExpect(MockMvcResultMatchers.content().string("08xxxxxxxx"))
				.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldChangePinFail() throws Exception {

		when(this.tmnProfileServiceMock.changePin(anyString(), any(ChangePin.class)))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));
		
		ObjectMapper mapper = new ObjectMapper();
		
		this.mockMvc.perform(put("/ewallet/profile/change-pin?channelID={channelID}&accessTokenID={accessTokenID}", "40", "TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(new ChangePin("0000", "1111"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("Error Code"))
				.andExpect(jsonPath("$.errorDescription").value("Error Description"))
				.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
		
	}
}
