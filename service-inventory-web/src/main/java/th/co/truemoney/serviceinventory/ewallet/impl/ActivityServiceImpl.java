package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	@Autowired @Qualifier("jsonHttpHeader")
	private HttpHeaders headers;
	
	@Autowired
	private EndPoints endPoints;
	
	@Override
	public List<Activity> getActivities(String accessTokenID) throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		
		ResponseEntity<Activity[]> response = restTemplate.exchange(endPoints.getListAllReport(), 
				HttpMethod.GET, new HttpEntity<String>(headers), Activity[].class, accessToken.getTruemoneyID());
		
		return Arrays.asList(response.getBody());
	}

	@Override
	public ActivityDetail getActivityDetail(Long reportID, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		
		ResponseEntity<ActivityDetail> response = restTemplate.exchange(endPoints.getReportDetail(), 
				HttpMethod.GET, new HttpEntity<String>(headers), ActivityDetail.class, accessToken.getTruemoneyID(), reportID );

		return response.getBody();
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepository = accessTokenRepo;
	}

}
