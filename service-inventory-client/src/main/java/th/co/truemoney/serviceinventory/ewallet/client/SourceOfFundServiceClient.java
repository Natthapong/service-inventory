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

import th.co.truemoney.serviceinventory.ewallet.SourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class SourceOfFundServiceClient implements SourceOfFundService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String username,
			String accessTokenID) throws ServiceInventoryException {
		HttpEntity<DirectDebit[]> requestEntity = new HttpEntity<DirectDebit[]>(headers);

		ResponseEntity<DirectDebit[]> responseEntity = restTemplate.exchange(
				environmentConfig.getUserDirectDebitSourceOfFundsUrl(),
					HttpMethod.GET, requestEntity, DirectDebit[].class, username, accessTokenID);

		DirectDebit[] directDebits = responseEntity.getBody();

		return Arrays.asList(directDebits);
	}

}
