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
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnProfileServiceClient implements TmnProfileService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public String login(Integer channelID, Login login) throws ServiceInventoryException {

		Validate.notNull(channelID, "channel id is required");
		Validate.notNull(login, "login bean is required");


		HttpEntity<Login> requestEntity = new HttpEntity<Login>(login, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(environmentConfig.getLoginUrl(), HttpMethod.POST, requestEntity, String.class, channelID);

		String accessToken = responseEntity.getBody();

		return accessToken;
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accesstokenID, String checksum) throws ServiceInventoryException {

		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(headers);

		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(
				environmentConfig.getUserProfileUrl(),
					HttpMethod.GET, requestEntity, TmnProfile.class, accesstokenID, checksum);

		TmnProfile tmnProfile = responseEntity.getBody();

		return tmnProfile;
	}

	
	@Override
	public BigDecimal getEwalletBalance(String accessTokenID, String checksum)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
	}

}
