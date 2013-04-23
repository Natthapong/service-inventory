package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;

public class TopupMobileServicesClient {
	
	@Autowired
	RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	private EndPoints endPoints;

	public EndPoints getEndPoints() {
		return endPoints;
	}

	public void setEndPoints(EndPoints endPoints) {
		this.endPoints = endPoints;
	}

	@Autowired
	private HttpHeaders headers;

	public HashMap verify(String accessTokenID,String targetMobileNumber,BigDecimal amount) {
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
				endPoints.getVerifyTopupMobile(), HttpMethod.POST,
				requestEntity, HashMap.class,accessTokenID);
		
		return responseEntity.getBody();
	}

}
