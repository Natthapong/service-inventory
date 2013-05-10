package th.co.truemoney.serviceinventory.ewallet.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;


public class FavoriteServicesClientTest {
	
	private FavoriteServicesClient client;
	
	private RestTemplate restTemplate;
	
	private EndPoints endPoints;
	
	private HttpHeaders headers;
	
	@Before
	public void setup() {
		restTemplate = mock(RestTemplate.class);
		client = new FavoriteServicesClient();
		
		endPoints = new EndPoints();
		client.setEndPoints(endPoints);
		
		headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		client.setRestTemplate(restTemplate);
	}
	
	@Test
	public void addFavorite() {
		Favorite favorite = new Favorite();
		
		ResponseEntity<Favorite> responseEntity = new ResponseEntity<Favorite>(favorite,HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getAddFavoriteURL()), eq(HttpMethod.POST), any(HttpEntity.class)
				, eq(Favorite.class), anyString()) ).thenReturn(responseEntity);
		
		Favorite favoriteResult = client.addFavorite(favorite, "12345");
		Assert.assertNotNull(favoriteResult);
	}
	
	@Test
	public void getFavorites() {		
		Favorite[] favorites = new Favorite[1];
		
		ResponseEntity<Favorite[]> responseEntity = new ResponseEntity<Favorite[]>(favorites,HttpStatus.OK);
		
		when(restTemplate.exchange(eq(endPoints.getFavoritesURL()), eq(HttpMethod.GET), any(HttpEntity.class)
				, eq(Favorite[].class) , anyString()) ).thenReturn(responseEntity);
		
		List<Favorite> favoriteResults = client.getFavorites("12345");
		Assert.assertNotNull(favoriteResults);
	}
}
