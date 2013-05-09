package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class FavoriteServicesClient implements FavoriteService{

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;
	
	@Override
	public Favorite addFavorite(Favorite favorite)
			throws ServiceInventoryException {
		
		HttpEntity<Favorite> requestEntity = new HttpEntity<Favorite>(favorite, headers);
		
		ResponseEntity<Favorite> responseEntity = restTemplate.exchange(
				endPoints.getAddFavoriteURL(), HttpMethod.POST,
				requestEntity, Favorite.class);

		return responseEntity.getBody();
	}

}
