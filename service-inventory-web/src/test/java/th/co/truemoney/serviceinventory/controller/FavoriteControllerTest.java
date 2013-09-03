package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class FavoriteControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private FavoriteService favoriteServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.favoriteServiceMock = wac.getBean(FavoriteService.class);
	}

	@After
	public void tierDown() {
		reset(this.favoriteServiceMock);
	}
	
	@Test
	public void shouldAddFavoriteSuccess() throws Exception {
		Favorite favorite = new Favorite();
		favorite.setFavoriteID(1000l);
		when(this.favoriteServiceMock.addFavorite(any(Favorite.class), anyString())).thenReturn(favorite);
		
		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(post("/ewallet/favorites/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(favorite)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.favoriteID").value(1000));
	}
	
	@Test
	public void shouldAddFavoriteFail() throws Exception {
		Favorite favorite = new Favorite();
		favorite.setFavoriteID(1000l);
		when(this.favoriteServiceMock.addFavorite(any(Favorite.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));
		
		ObjectMapper mapper = new ObjectMapper();
		this.mockMvc.perform(post("/ewallet/favorites/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(favorite)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
	@Test
	public void shouldRemoveFavoritesSuccess() throws Exception {
		when(this.favoriteServiceMock.deleteFavorite(anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
		
		this.mockMvc.perform(delete("/ewallet/favorites?accessTokenID={accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())	
			.andExpect(MockMvcResultMatchers.content().string("true"));
	}
	
	@Test
	public void shouldRemoveFavoritesFail() throws Exception {
		when(this.favoriteServiceMock.deleteFavorite(anyString(), anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));
		
		this.mockMvc.perform(delete("/ewallet/favorites?accessTokenID={accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
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
	public void shouldGetFavoritesFail() throws Exception {
		Favorite favorite = new Favorite();
		favorite.setFavoriteID(1000l);
		List<Favorite> favorites = new ArrayList<Favorite>();
		favorites.add(favorite);
		
		when(this.favoriteServiceMock.getFavorites(anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));
		
		this.mockMvc.perform(get("/ewallet/favorites?accessTokenID={accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
}
