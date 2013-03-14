package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

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

import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, TestServiceInventoryConfig.class })
public class SourceOfFundControllerSuccessTest {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private SourceOfFundService sourceOfFundServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.sourceOfFundServiceMock = wac.getBean(SourceOfFundService.class);
	}

	@After
	public void tierDown() {
		reset(this.sourceOfFundServiceMock);
	}
	
	@Test
	public void shouldSuccess() throws Exception {
		
		//given 
		when(sourceOfFundServiceMock.getDirectDebitSources(anyInt(), anyString(), anyString()))
			.thenReturn(new ArrayList<DirectDebit>());
		
		this.mockMvc.perform(get("/{username}/source-of-fund/direct-debits?channelID=41&accessToken=e6701de94fdda4347a3d31ec5c892ccadc88b847", "user1.test.v1@gmail.com")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())	
			.andDo(print());	
		
	}
		
}