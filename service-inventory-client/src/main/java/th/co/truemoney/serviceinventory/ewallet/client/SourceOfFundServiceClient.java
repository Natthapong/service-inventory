package th.co.truemoney.serviceinventory.ewallet.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;

@Service
public class SourceOfFundServiceClient implements SourceOfFundService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@SuppressWarnings("unchecked")
	@Override
	public List<DirectDebit> getDirectDebitSources(Integer channelID, String username, String accessToken) {

		HttpEntity<List<DirectDebit>> requestEntity = new HttpEntity<List<DirectDebit>>(headers);

		ResponseEntity<List> responseEntity = restTemplate.exchange(
				environmentConfig.getUserDirectDebitSourceOfFundsUrl(),
					HttpMethod.GET, requestEntity, List.class, "username", channelID, accessToken);
		
		List<DirectDebit> directDebits = responseEntity.getBody();
		
		return directDebits;
	}

}
