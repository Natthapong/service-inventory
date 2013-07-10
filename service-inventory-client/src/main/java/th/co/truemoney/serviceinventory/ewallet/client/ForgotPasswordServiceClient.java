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
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class ForgotPasswordServiceClient implements ForgotPasswordService {

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
	public ForgotPassword createForgotPassword(Integer channelID, ForgotPassword request) throws ServiceInventoryException {
		HttpEntity<ForgotPassword> requestEntity = new HttpEntity<ForgotPassword>(request, headers);
		
		ResponseEntity<ForgotPassword> responseEntity = restTemplate.exchange(
				endPoints.getRequestForgotPasswordURL(), HttpMethod.POST,
				requestEntity,ForgotPassword.class, channelID);

		return responseEntity.getBody();
		
	}

	@Override
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request) throws ServiceInventoryException {
		
		HttpEntity<ResetPassword> requestEntity = new HttpEntity<ResetPassword>(request, headers);
		
		ResponseEntity<VerifyResetPassword> responseEntity = restTemplate.exchange(
				endPoints.getVerifyResetPasswordURL(), HttpMethod.POST,
				requestEntity, VerifyResetPassword.class, channelID);

		return responseEntity.getBody();
		
	}
	
	@Override
	public String verifyOTP(Integer channelID, VerifyResetPassword verifyResetPassword) throws ServiceInventoryException {
		
		HttpEntity<VerifyResetPassword> requestEntity = new HttpEntity<VerifyResetPassword>(verifyResetPassword, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				endPoints.getVerifyOTPResetPasswordURL(), HttpMethod.POST,
				requestEntity, String.class, channelID);

		return responseEntity.getBody();
		
	}
	
	@Override
	public String confirmResetPassword(Integer channelID, ResetPassword request) throws ServiceInventoryException {
		HttpEntity<ResetPassword> requestEntity = new HttpEntity<ResetPassword>(request, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(
				endPoints.getComfirmResetPasswordURL(), HttpMethod.POST,
				requestEntity, String.class, channelID);

		return responseEntity.getBody();
	}

	@Override
	public VerifyResetPassword resendOTP(Integer channelID, String resetPasswordID) {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<VerifyResetPassword> responseEntity = restTemplate.exchange(
				endPoints.getResendOTPResetPasswordURL(), HttpMethod.POST,
				requestEntity, VerifyResetPassword.class, resetPasswordID, channelID);

		return responseEntity.getBody();
	}
	
	

}
