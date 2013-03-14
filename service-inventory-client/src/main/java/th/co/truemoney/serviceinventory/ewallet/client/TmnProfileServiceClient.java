package th.co.truemoney.serviceinventory.ewallet.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitSource;
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
	public String login(Integer channelID, Login login) {
		
		HttpEntity<Login> requestEntity = new HttpEntity<Login>(login, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(environmentConfig.getLoginUrl(), HttpMethod.POST, requestEntity, String.class);
		
		String accessToken = responseEntity.getBody();
		
		return accessToken;
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accesstoken, String checksum,
			Integer channelID) {
		return null;
	}

	@Override
	public List<DirectDebitSource> getDirectDebitSources(Integer ChannelID,
			String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void Logout(String accessToken, Integer ChannelID) {
		// TODO Auto-generated method stub
		
	}
	
}
