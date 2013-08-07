package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class GetProfileControllerTest {

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
	public void shouldGetProfileSuccess() throws Exception {
		
		//given
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname("fullname");
		tmnProfile.setMobileNumber("086xxxxxxx");
		tmnProfile.setThaiID("1212121212121");		
		tmnProfile.setHasPassword(Boolean.TRUE);
		tmnProfile.setHasPin(Boolean.FALSE);
		tmnProfile.setImageURL("https://m.truemoney.co.th/images/xxx.jsp");
		when(this.tmnProfileServiceMock.getTruemoneyProfile(anyString())).thenReturn(tmnProfile);
		
		//perform
		this.mockMvc.perform(get("/ewallet/profile/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fullname").exists())
			.andExpect(jsonPath("$.thaiID").exists())
			.andExpect(jsonPath("$.mobileNumber").exists())
			.andExpect(jsonPath("$.hasPassword").exists())
			.andExpect(jsonPath("$.hasPin").exists())
			.andExpect(jsonPath("$.imageURL").exists());
		
	}
	
	@Test
	public void shouldGetProfileFail() throws Exception {
		
		//given
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname("fullname");
		tmnProfile.setMobileNumber("086xxxxxxx");
		tmnProfile.setThaiID("1212121212121");		
		tmnProfile.setHasPassword(Boolean.TRUE);
		tmnProfile.setHasPin(Boolean.FALSE);
		tmnProfile.setImageURL("https://m.truemoney.co.th/images/xxx.jsp");
		when(this.tmnProfileServiceMock.getTruemoneyProfile(anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));
		
		//perform
		this.mockMvc.perform(get("/ewallet/profile/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
		
	}
	
}
