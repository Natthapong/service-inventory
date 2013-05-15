package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.bean.LoginRequest;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.SmsConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;


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
		
		EWalletOwnerCredential userLogin = new EWalletOwnerCredential("user1.test.v1@gmail.com", "e6701de94fdda4347a3d31ec5c892ccadc88b847", 40);
		ClientCredential clientLogin = new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail");

		LoginRequest loginRequest = new LoginRequest(userLogin, clientLogin);


		this.mockMvc.perform(post("/ewallet/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(content().string("8e48e03be057319f40621fe9bcd123f750f6df1d"));
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

}