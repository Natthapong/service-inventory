package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TmnProfileControllerLoginSuccessTest {

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
	public void shouldLoginSuccess() throws Exception {

		when(this.tmnProfileServiceMock.login(
				any(EWalletOwnerCredential.class),
				any(ClientCredential.class)))
				.thenReturn("8e48e03be057319f40621fe9bcd123f750f6df1d");
		
		EWalletOwnerCredential userLogin = new EWalletOwnerCredential("local@tmn.com", "password", 40);
		ClientCredential clientLogin = new ClientCredential("myAppKey", "myAppUser", "myAppPassword", "iPhone", "iPhone");

		LoginRequest loginRequest = new LoginRequest(userLogin, clientLogin);


		this.mockMvc.perform(post("/ewallet/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(loginRequest)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldLogoutSuccess() throws Exception {
		
		when(this.tmnProfileServiceMock.logout(anyString())).thenReturn("");
		
		this.mockMvc.perform(post("/ewallet/logout/{accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldGetBalanceSuccess() throws Exception {
		
		//given
		when(this.tmnProfileServiceMock.getEwalletBalance(anyString())).thenReturn(new BigDecimal("100.00"));
		
		//perform
		this.mockMvc.perform(get("/ewallet/profile/balance/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldValidateEmailSuccess() throws Exception {
		
		when(this.tmnProfileServiceMock.validateEmail(anyInt(), anyString())).thenReturn("local@tmn.com");
		
		this.mockMvc.perform(post("/ewallet/profile/validate-email?channelID={channelID}", "40")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes("local@tmn.com")))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldCreateTrueMoneyProfileSucesss() throws Exception {
		
		when(this.tmnProfileServiceMock.createProfile(anyInt(), any(TmnProfile.class))).thenReturn(new OTP("0866013468", "adgf"));
		
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname("fullname");
		tmnProfile.setMobileNumber("086xxxxxxx");
		tmnProfile.setThaiID("1212121212121");	
		
		this.mockMvc.perform(post("/ewallet/profile?channelID={channelID}", "40")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(tmnProfile)))
			.andExpect(status().isOk());
		
	}

	@Test
	public void shouldConfirmCreateTrueMoneyProfileSuccess() throws Exception {
		
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname("fullname");
		tmnProfile.setMobileNumber("086xxxxxxx");
		tmnProfile.setThaiID("1212121212121");	
		
		when(this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class))).thenReturn(tmnProfile);
		
		this.mockMvc.perform(post("/ewallet/profile/verify-otp?channelID={channelID}", "40")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(new OTP("0866013468", "adgf"))))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldConfirmCreateTrueMoneyProfileNotSendChannelID() throws Exception {
		
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setFullname("fullname");
		tmnProfile.setMobileNumber("086xxxxxxx");
		tmnProfile.setThaiID("1212121212121");	
		
		when(this.tmnProfileServiceMock.confirmCreateProfile(anyInt(), any(OTP.class))).thenReturn(tmnProfile);
		
		this.mockMvc.perform(post("/ewallet/profile/verify-otp")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(new OTP("0866013468", "adgf"))))
			.andExpect(status().is(412));
	
	}
	
	@Test
	public void verifyTokenSuccess() throws Exception {
		//given
		when(tmnProfileServiceMock.verifyAccessToken(anyString())).thenReturn("TokenID");
		
		//perform
		this.mockMvc.perform(get("/ewallet/verify-token/{accessTokenID}", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());		
	}
	
	@Test
	public void verifyTokenFail() throws Exception {
		//given
		when(tmnProfileServiceMock.verifyAccessToken(anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		
		//perform
		this.mockMvc.perform(get("/ewallet/verify-token/{accessTokenID}", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));	
	}
	
}