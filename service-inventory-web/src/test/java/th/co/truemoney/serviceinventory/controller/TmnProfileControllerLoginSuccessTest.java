package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TmnProfileControllerLoginSuccessTest {

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;
	
	@Autowired
	private ActivityService activityServiceMock;
	
	@Autowired
	private FavoriteService favoriteServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);
		this.favoriteServiceMock = wac.getBean(FavoriteService.class);
		
		mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.tmnProfileServiceMock);
		reset(this.favoriteServiceMock);
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
	public void shouldGetBalanceSuccess() throws Exception {
		
		//given
		when(this.tmnProfileServiceMock.getEwalletBalance(anyString())).thenReturn(new BigDecimal("100.00"));
		
		//perform
		this.mockMvc.perform(get("/ewallet/profile/balance/{accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldGetActivitySuccess() throws Exception {
		Activity activity = new Activity();
		activity.setReportID(1000l);
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		
		when(this.activityServiceMock.getActivities(anyString())).thenReturn(activities);
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())	
		.andExpect(jsonPath("$.[0].reportID").value(1000));
	}
	
	@Test
	public void shouldGetActivityDetailSuccess() throws Exception {
		ActivityDetail activity = new ActivityDetail();
		activity.setTransactionID("xxx");
		
		when(this.activityServiceMock.getActivityDetail(anyLong(), anyString())).thenReturn(activity);
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}/detail/{reportID}", "12345", 1000l)
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())	
		.andExpect(jsonPath("$.transactionID").value("xxx"));
		
	}
	
	@Test
	public void shouldLogoutSuccess() throws Exception {
		
		when(this.tmnProfileServiceMock.logout(anyString())).thenReturn("");
		
		this.mockMvc.perform(post("/ewallet/logout/{accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldAddFavoriteSuccess() throws Exception {
		Favorite favorite = new Favorite();
		favorite.setFavoriteID(1000l);
		when(this.favoriteServiceMock.addFavorite(any(Favorite.class), anyString())).thenReturn(favorite);
		
		this.mockMvc.perform(post("/ewallet/favorites/{accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON)
		.content(mapper.writeValueAsBytes(favorite)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.favoriteID").value(1000));
	}
	
	@Test
	public void shouldGetFavoritesSuccess() throws Exception {
		Favorite favorite = new Favorite();
		favorite.setFavoriteID(1000l);
		List<Favorite> favorites = new ArrayList<Favorite>();
		favorites.add(favorite);
		
		when(this.favoriteServiceMock.getFavorites(anyString())).thenReturn(favorites);
		
		this.mockMvc.perform(get("/ewallet/favorites?accessTokenID={accessTokenID}", "12345")
		.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())	
		.andExpect(jsonPath("$.[0].favoriteID").value(1000));
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
	
}