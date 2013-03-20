package th.co.truemoney.serviceinventory.ewallet.client;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnTopUpServiceClient implements TopUpService {
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID, 
			QuoteRequest quoteRequest , String accessTokenID) throws ServiceInventoryException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("sourceOfFundID", sourceOfFundID);
		params.put("accessTokenID", accessTokenID);
		
		String url = MessageFormat.format(environmentConfig.getCreateTopUpQuoteFromDirectDebitUrl(), params);
		
		ResponseEntity<TopUpQuote> responseEntity = restTemplate.postForEntity(url, quoteRequest, TopUpQuote.class);
		return responseEntity.getBody();
	}
	
	@Override
	public TopUpOrder confirmPlaceOrder(String arg0, OTP arg1, String arg2)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder getTopUpOrderDetails(String arg0, String arg1)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String arg0, String arg1)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String arg0, String arg1)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpOrder requestPlaceOrder(String arg0, String arg1)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
