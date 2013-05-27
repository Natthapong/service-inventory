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

import th.co.truemoney.serviceinventory.ewallet.DirectDebitSourceOfFundService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnDirectDebitSourceOfFundServiceClient implements DirectDebitSourceOfFundService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EndPoints environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public List<DirectDebit> getUserDirectDebitSources(String accessTokenID) throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<DirectDebit[]> responseEntity = restTemplate.exchange(
				environmentConfig.getUserDirectDebitSourceOfFundsURL(), HttpMethod.GET,
				requestEntity, DirectDebit[].class,
				accessTokenID);

		return Arrays.asList(responseEntity.getBody());
	}
}
