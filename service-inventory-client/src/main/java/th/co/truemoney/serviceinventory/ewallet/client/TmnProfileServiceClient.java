package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnProfileServiceClient implements TmnProfileService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public String login(Integer channelID, Login login) throws ServiceInventoryException {

		Validate.notNull(channelID, "channel id is required");
		Validate.notNull(login, "login bean is required");


		HttpEntity<Login> requestEntity = new HttpEntity<Login>(login, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getLoginUrl(), HttpMethod.POST, requestEntity, String.class, channelID);

		String accessToken = responseEntity.getBody();

		return accessToken;
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accesstokenID) throws ServiceInventoryException {

		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(headers);

		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(
				endPoints.getUserProfileUrl(),
					HttpMethod.GET, requestEntity, TmnProfile.class, accesstokenID);

		TmnProfile tmnProfile = responseEntity.getBody();

		return tmnProfile;
	}


	@Override
	public BigDecimal getEwalletBalance(String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<BigDecimal> requestEntity = new HttpEntity<BigDecimal>(headers);
		ResponseEntity<BigDecimal> responseEntity = restTemplate.exchange(
				endPoints.getBalance(),
					HttpMethod.GET, requestEntity, BigDecimal.class, accessTokenID);

		return responseEntity.getBody();

	}

	@Override
	public String logout(String accessTokenID) throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getLogoutUrl(), HttpMethod.POST, requestEntity, String.class, accessTokenID);
		return responseEntity.getBody();
	}

	@Override
	public String validateEmail(Integer channelID, String email) {
		HttpEntity<String> requestEntity = new HttpEntity<String>(email,headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getValidateEmailUrl(), HttpMethod.POST, requestEntity, String.class, channelID);
		return responseEntity.getBody();
	}

	@Override
	public OTP sendOTPConfirm(Integer channelID, TmnProfile tmnProfile) {
		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(tmnProfile,headers);
		ResponseEntity<OTP> responseEntity = restTemplate.exchange(endPoints.getCreateTruemoneyProfileUrl(), HttpMethod.POST, 
				requestEntity, OTP.class, channelID);
		return responseEntity.getBody();
	}

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) {
		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp,headers);
		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(endPoints.getConfirmCreateTruemoneyProfileUrl(), HttpMethod.POST, 
				requestEntity, TmnProfile.class, channelID);
		return responseEntity.getBody();
	}

}
