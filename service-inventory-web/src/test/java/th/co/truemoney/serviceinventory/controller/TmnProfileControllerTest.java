package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})

public class TmnProfileControllerTest {

private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.tmnProfileServiceMock);
	}

	@Test
	public void turnOffProfileImageStatus() throws Exception {
		this.mockMvc.perform(post("/ewallet/profile/change-image-status/0000000000")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(Boolean.FALSE)))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk());
		
		Mockito.verify(this.tmnProfileServiceMock).changeProfileImageStatus(anyString(), eq(Boolean.FALSE));
	}

	@Test
	public void turnOnProfileImageStatus() throws Exception {
		this.mockMvc.perform(post("/ewallet/profile/change-image-status/0000000000")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(Boolean.TRUE)))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk());
		
		Mockito.verify(this.tmnProfileServiceMock).changeProfileImageStatus(anyString(), eq(Boolean.TRUE));
	}

}
