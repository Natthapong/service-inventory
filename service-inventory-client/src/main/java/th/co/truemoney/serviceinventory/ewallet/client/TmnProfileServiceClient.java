package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnProfileServiceClient implements TmnProfileService {

	@Autowired
	private RestTemplate restTemplate;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public String login(EWalletOwnerLogin userLogin, ClientLogin clientLogin, ChannelInfo channelInfo) throws ServiceInventoryException {

		Validate.notNull(channelInfo, "channel info is required");
		Validate.notNull(userLogin, "user login is required");
		Validate.notNull(clientLogin, "client login is required");

		HashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("userLogin", userLogin);
		map.put("appLogin", clientLogin);
		map.put("channelInfo", channelInfo);

		HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<HashMap<String, Object>>(map, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getLoginURL(), HttpMethod.POST, requestEntity, String.class);

		String accessToken = responseEntity.getBody();

		return accessToken;
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accesstokenID) throws ServiceInventoryException {

		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(headers);

		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(
				endPoints.getUserProfileURL(),
					HttpMethod.GET, requestEntity, TmnProfile.class, accesstokenID);

		TmnProfile tmnProfile = responseEntity.getBody();

		return tmnProfile;
	}


	@Override
	public BigDecimal getEwalletBalance(String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<BigDecimal> requestEntity = new HttpEntity<BigDecimal>(headers);
		ResponseEntity<BigDecimal> responseEntity = restTemplate.exchange(
				endPoints.getBalanceURL(),
					HttpMethod.GET, requestEntity, BigDecimal.class, accessTokenID);

		return responseEntity.getBody();

	}

	@Override
	public String logout(String accessTokenID) throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getLogoutURL(), HttpMethod.POST, requestEntity, String.class, accessTokenID);
		return responseEntity.getBody();
	}

	@Override
	public String validateEmail(Integer channelID, String email) {
		HttpEntity<String> requestEntity = new HttpEntity<String>(email,headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(endPoints.getValidateEmailURL(), HttpMethod.POST, requestEntity, String.class, channelID);
		return responseEntity.getBody();
	}

	@Override
	public OTP createProfile(Integer channelID, TmnProfile tmnProfile) {
		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(tmnProfile,headers);
		ResponseEntity<OTP> responseEntity = restTemplate.exchange(endPoints.getCreateTruemoneyProfileURL(), HttpMethod.POST,
				requestEntity, OTP.class, channelID);
		return responseEntity.getBody();
	}

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) {
		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp,headers);
		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(endPoints.getConfirmCreateTruemoneyProfileURL(), HttpMethod.POST,
				requestEntity, TmnProfile.class, channelID);
		return responseEntity.getBody();
	}

}
