package th.co.truemoney.serviceinventory.ewallet.client;

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

@Service
public class TmnProfileServiceClient implements TmnProfileService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public String login(Integer channelId, Login login) {

		HttpEntity<Login> requestEntity = new HttpEntity<Login>(login, headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange(environmentConfig.getLoginUrl(), HttpMethod.POST, requestEntity, String.class, channelId);

		String accessToken = responseEntity.getBody();

		return accessToken;
	}

	@Override
	public TmnProfile getTruemoneyProfile(Integer channelId,
			String accesstoken, String checksum) {
		
		HttpEntity<TmnProfile> requestEntity = new HttpEntity<TmnProfile>(headers);

		ResponseEntity<TmnProfile> responseEntity = restTemplate.exchange(
				environmentConfig.getUserProfileUrl(),
					HttpMethod.GET, requestEntity, TmnProfile.class, accesstoken, checksum, channelId);
		
		TmnProfile tmnProfile = responseEntity.getBody();
		
		return tmnProfile;
	}

	@Override
	public void logout(Integer ChannelID, String accessToken) {
		
	}

}
