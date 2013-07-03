package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class ForgotPasswordServiceClient implements ForgotPasswordService{

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EndPoints endPoints;
	
	@Autowired
	private HttpHeaders headers;
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setEndPoints(EndPoints endPoints) {
		this.endPoints = endPoints;
	}
	
	@Override
	public ForgotPassword requestForgotPassword(ForgotPassword request) throws ServiceInventoryException{
		
		HttpEntity<ForgotPassword> requestEntity = new HttpEntity<ForgotPassword>(request, headers);
		
		ResponseEntity<ForgotPassword> responseEntity = restTemplate.exchange(
				endPoints.getRequestForgotPasswordURL(), HttpMethod.POST,
				requestEntity,ForgotPassword.class);

		return responseEntity.getBody();
	}

}
