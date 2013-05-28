package th.co.truemoney.serviceinventory.ewallet.client;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FavoriteServicesClient implements FavoriteService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EndPoints endPoints;
	
	@Autowired
	private HttpHeaders headers;
	
	@Override
	public Favorite addFavorite(Favorite favorite, String accessTokenID)
			throws ServiceInventoryException {
		
		HttpEntity<Favorite> requestEntity = new HttpEntity<Favorite>(favorite, headers);
		
		ResponseEntity<Favorite> responseEntity = restTemplate.exchange(
				endPoints.getAddFavoriteURL(), HttpMethod.POST,
				requestEntity, Favorite.class, accessTokenID);

		return responseEntity.getBody();
	}
	
	@Override
	public void deleteFavorite(String billCode,String ref1, String accessTokenID)
			throws ServiceInventoryException {
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		restTemplate.exchange(endPoints.getDeleteFavoriteURL(), HttpMethod.DELETE,
				requestEntity, Boolean.class, billCode, ref1, accessTokenID);
		
	}
	
    @Override
    public List<Favorite> getFavorites(String accessTokenID) throws ServiceInventoryException {
    	HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Favorite[]> responseEntity = restTemplate.exchange(
				endPoints.getFavoritesURL(), HttpMethod.GET,

				requestEntity, Favorite[].class, accessTokenID);
        return Arrays.asList(responseEntity.getBody());
    }
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setEndPoints(EndPoints endPoints) {
		this.endPoints = endPoints;
	}

}
