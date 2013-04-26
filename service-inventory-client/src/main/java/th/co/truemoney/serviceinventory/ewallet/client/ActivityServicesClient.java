package th.co.truemoney.serviceinventory.ewallet.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class ActivityServicesClient implements ActivityService{

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private EndPoints endPoints;
	
	@Autowired
	private HttpHeaders headers;
	
	@Override
	public List<Activity> getActivities(String accessTokenID)
			throws ServiceInventoryException {
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<Activity[]> responseEntity = restTemplate.exchange(
				endPoints.getActivitiesListURL(), HttpMethod.GET,
				requestEntity, Activity[].class, accessTokenID);
		
		return Arrays.asList(responseEntity.getBody());
	}

	@Override
	public ActivityDetail getActivityDetail(Long reportID,
			String accessTokenID) throws ServiceInventoryException {
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<ActivityDetail> responseEntity = restTemplate.exchange(
				endPoints.getActivityDetailURL(), HttpMethod.GET,
				requestEntity, ActivityDetail.class, accessTokenID , reportID);
		
		return responseEntity.getBody();
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public EndPoints getEndPoints() {
		return endPoints;
	}

	public void setEndPoints(EndPoints endPoints) {
		this.endPoints = endPoints;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}	
}
